package reactivepostgresql.auth

import java.security.InvalidParameterException
import java.time.{Duration, ZonedDateTime}
import java.util.UUID

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import com.restfb.types.{User => FbUser}
import com.restfb.{DefaultFacebookClient, Version}
import reactivepostgresql.auth.be._
import reactivepostgresql.auth.repositories.{AuthTokenRepository, UserRepository}
import reactivepostgresql.common.ErrorWrapper
import reactivepostgresql.common.ResponseWrapper.ResponseWrapper
import reactivepostgresql.config.{AuthConfig, FbConfig}
import reactivepostgresql.profiles.ProfileDto.ProfileUpdateRequest
import reactivepostgresql.profiles.ProfilesService
import reactivepostgresql.profiles.be.Profile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class AuthService(userRepository: UserRepository, authTokenRepository: AuthTokenRepository, authMailer: AuthMailer, authConfig: AuthConfig, logger: LoggingAdapter,
                  fbConfig: FbConfig, profileService: ProfilesService)
                 (implicit ec: ExecutionContext, actorSystem: ActorSystem) {

  def register(request: RegisterRequest): ResponseWrapper[String] =
    userRepository.findByEmailAddress(request.email).map {
      case Some(exisitingUser) if (exisitingUser.activated) =>
        authMailer.sendResetPasswordInstructions(request.email)
        Right("OK")
      case Some(exisitingUser) =>
        val token = Token.generate
        authMailer.sendActivationEmail(exisitingUser.emailAddress, token)
        userRepository.update(exisitingUser.copy(emailToken = Some(token), emailTokenValidTo = Some(ZonedDateTime.now.plus(authConfig.emailTokenTtl))))
        Right("OK")
      case None =>
        val token = Token.generate
        val user = User(emailAddress = request.email, passwordHash = Some(PasswordHash.hash(request.password)),
          emailToken = Some(token), emailTokenValidTo = Some(ZonedDateTime.now.plus(authConfig.emailTokenTtl)), facebookId = None)
        userRepository.insert(user)
        authMailer.sendActivationEmail(user.emailAddress, token)
        Right("OK")
    }

  def activate(activate: ActivationRequest): ResponseWrapper[AuthToken] =
    userRepository.findByEmailToken(activate.token).flatMap {
      case Some(exisitingUser)
        if (ZonedDateTime.now.isBefore(exisitingUser.emailTokenValidTo.getOrElse(throw new IllegalArgumentException("")))) =>
        userRepository.update(exisitingUser.copy(activated = true)).flatMap(num =>
          localLogin(exisitingUser.uuid, AuthProvider.EmailPassword, None).map {
            Right(_)
          }
        )
      case Some(exisitingUser) =>
        Future.successful(Left(ErrorWrapper("tokenExpires", "Token expires")))
      case None =>
        Future.successful(Left(ErrorWrapper("invalidToken", "Invalid token")))
    }

  def login(loginRequest: LoginRequest): ResponseWrapper[AuthToken] =
    userRepository.findByEmailAddress(loginRequest.email).flatMap {
      case Some(user) =>
        user.passwordHash match {
          case Some(passwordHash) if PasswordHash(passwordHash.hash).matches(loginRequest.password) =>
            user.activated match {
              case true =>
                val authToken = AuthToken(Token.generate, user.uuid, ZonedDateTime.now().plus(authConfig.sessionLongTtl))
                localLogin(user.uuid, AuthProvider.EmailPassword, None).map { authToken =>
                  Right(authToken)
                }
              case false =>
                Future.successful(Left(ErrorWrapper("notActivated", "accountNotActivatedYet")))
            }
          case _ =>
            Future.successful(Left(ErrorWrapper("passwordNotMatch", "password not match")))
        }
      case _ => Future.successful(Left(ErrorWrapper("passwordNotMatch", "password not match")))
    }


  def requestPasswordReset(resetPasswordRequest: ResetPasswordRequest): ResponseWrapper[String] =
    userRepository.findByEmailAddress(resetPasswordRequest.email).map {
      case Some(user) if (user.activated) =>
        val emailToken = Token.generate
        val emailTokenValidTo = ZonedDateTime.now().plus(authConfig.emailTokenTtl)
        val updatedUser = user.copy(emailToken = Option(emailToken), emailTokenValidTo = Option(emailTokenValidTo))
        userRepository.update(updatedUser)
        authMailer.sendResetPasswordLink(user.emailAddress, emailToken)
        Right("OK")
      case Some(user) =>
        val emailToken = Token.generate
        val emailTokenValidTo = ZonedDateTime.now().plus(authConfig.emailTokenTtl)
        val updatedUser = user.copy(emailToken = Option(emailToken), emailTokenValidTo = Option(emailTokenValidTo))
        userRepository.update(updatedUser)
        authMailer.sendActivationEmail(user.emailAddress, emailToken)
        Right("OK")
      case (_) =>
        Right("OK")
    }


  def confirmResetPassword(request: ConfirmResetPasswordRequest): ResponseWrapper[String] =
    userRepository.findByEmailToken(request.token).flatMap {
      case Some(user) =>
        user.emailTokenValidTo match {
          case Some(emailTokenValidTo) if ZonedDateTime.now.isBefore(emailTokenValidTo) =>
            user.activated match {
              case true =>
                val updatedUser = user.copy(emailToken = None, emailTokenValidTo = None, passwordHash = Some(PasswordHash.hash(request.password)))
                userRepository.update(updatedUser).map { num =>
                  Right("Ok")
                }
              case false =>
                Future.successful(Left(ErrorWrapper("notActivated", "accountNotActivatedYet")))
            }
          case None =>
            Future.successful(Left(ErrorWrapper("passwordNotMatch", "password not match")))
        }
      case None => Future.successful(Left(ErrorWrapper("invalidToken", "Invalid token")))
    }

  def fbLogin(oauthRequest: OAuthRequestWithToken): ResponseWrapper[AuthToken] =
    getFbUserDetails(oauthRequest.accessToken) match {
      case Success(fbUser) =>
        userRepository.findByFbId(fbUser.getId).flatMap {
          case Some(user) =>
            localLogin(user.uuid, AuthProvider.Facebook, Some(oauthRequest)).map {
              Right(_)
            }
          case None =>
            registerViaFb(fbUser, oauthRequest)
        }
      case Failure(e) =>
        Future.successful(Left(ErrorWrapper("serverError", "Can't connect to facebook server", Some(e.getMessage))))
    }

  def logout(tokenValue: Token): ResponseWrapper[Int] =
    authTokenRepository.deleteByValue(tokenValue).map {
      Right(_)
    }


  def authenticate(tokenValue: Token): ResponseWrapper[(User, AuthToken)] =
    authTokenRepository.findByValueWithUser(tokenValue).map {
      case Some((authToken, user)) if authToken.validTo.isAfter(ZonedDateTime.now()) =>
        if (authToken.prolong == true && Duration.between(ZonedDateTime.now(), authToken.validTo).compareTo(authConfig.sessionLongTtl) < 0) {
          val refreshedAuthToken = authToken.copy(validTo = ZonedDateTime.now().plus(authConfig.sessionLongTtl))
          authTokenRepository.update(refreshedAuthToken)
        }
        Right((user, authToken))
      case Some((authToken, user)) =>
        Left(ErrorWrapper("tokenExpired", "Auth token expired"))
      case _ =>
        Left(ErrorWrapper("invalidToken", "Invalid auth token"))
    }

  private def getFbUserDetails(accessToken: String): Try[FbUser] = {
    Try {
      val client = new DefaultFacebookClient(accessToken, fbConfig.secret, Version.VERSION_2_2)
      client.fetchObject("me", classOf[FbUser])
    }
  }

  private def localLogin(userUuid: UUID, authProvider: AuthProvider, oAuthRequestWithToken: Option[OAuthRequestWithToken]): Future[AuthToken] = {
    val now = ZonedDateTime.now()
    //take 10 seconds to be sure that expiresIn time is correct
    val authToken = (authProvider, oAuthRequestWithToken) match {
      case (AuthProvider.EmailPassword, None) => AuthToken(Token.generate, userUuid, ZonedDateTime.now().plus(authConfig.sessionLongTtl), true)
      case (AuthProvider.Facebook, Some(token)) => AuthToken(Token.generate, userUuid, ZonedDateTime.now().plusSeconds(token.expiresIn - 10), false)
      case (_, _) => throw new InvalidParameterException("bad local login data")
    }
    authTokenRepository.insert(authToken) //FIXME should i delete all other tokens
  }

  private def updateProfile(fbUser: FbUser, uuid: UUID): Future[Profile] = {
    profileService.updateAnonymous(uuid, ProfileUpdateRequest(
      nick = if (fbUser.getEmail != null) Some(fbUser.getName) else None,
      avatar = Some(s"http://graph.facebook.com/${fbUser.getId}/picture?height=300&width=300")
    ))
  }

  private def registerViaFb(fbUser: FbUser, oAuthRequestWithToken: OAuthRequestWithToken): ResponseWrapper[AuthToken] = {
    logger.debug(s"Creating new fb user. UserId:${fbUser.getId}")
    if (fbUser.getEmail != null) {
      userRepository.findByEmailAddress(EmailAddress(fbUser.getEmail)).flatMap {
        case Some(exisitingUser) =>
          userRepository.update(exisitingUser.copy(facebookId = Some(fbUser.getId)))
          updateProfile(fbUser, exisitingUser.uuid)
          localLogin(exisitingUser.uuid, AuthProvider.Facebook, Some(oAuthRequestWithToken)).map { authToken =>
            Right(authToken)
          }
        case None => {
          userRepository.insert(User(emailAddress = EmailAddress(fbUser.getEmail), passwordHash = None, facebookId = Some(fbUser.getId))).flatMap { user =>
            updateProfile(fbUser, user.uuid)
            localLogin(user.uuid, AuthProvider.Facebook, Some(oAuthRequestWithToken)).map {
              Right(_)
            }
          }
        }
      }
    } else {
      Future.successful(Left(ErrorWrapper("emptyEmail", "Please allow app to get email from facebook")))
    }
  }
}