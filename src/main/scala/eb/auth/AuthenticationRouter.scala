package eb.auth

import eb.auth.services._
import eb.common.CommonJsonProtocol
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport._
import spray.json._
import spray.routing.Directives._

import scala.concurrent.ExecutionContext

object AuthenticationRouter extends CommonJsonProtocol {
  implicit val emailAddressJsonFormat = jsonFlatFormat(EmailAddress.apply)
  implicit val passwordJsonFormat = jsonFlatFormat(Password.apply)
  implicit val loginRequestJsonFormat = jsonFormat2(LoginRequest)
  implicit val tokenJsonFormat = jsonFlatFormat(Token.apply)
  implicit val authToken = jsonFormat4(AuthToken)
  implicit val createUserRequestJsonFormat = jsonFormat2(RegisterRequest)
  implicit val resetPasswordRequest = jsonFormat1(ResetPasswordRequest)
  implicit val confirmResetPasswordRequest = jsonFormat2(ConfirmResetPasswordRequest)
  implicit val oAuthRequestWithToken = jsonFormat2(OAuthRequestWithToken)
  implicit val activationRequest = jsonFormat1(ActivationRequest)
}

class AuthenticationRouter(authService: AuthService, authenticator: Authenticator)(implicit ec: ExecutionContext) {

  import AuthenticationRouter._

  val route =
    pathPrefix("auth") {
      (post & pathPrefix("register") & pathEnd & entity(as[RegisterRequest])) { request =>
        complete {
          authService.register(request)
        }
      } ~ (post & pathPrefix("activate") & pathEnd & entity(as[ActivationRequest])) { request =>
        complete {
          authService.activate(request)
        }
      } ~ (post & pathPrefix("login") & pathEnd & entity(as[LoginRequest])) { request =>
        complete {
          authService.login(request)
        }
      } ~ (post & pathPrefix("password") & pathPrefix("reset") & pathEnd & entity(as[ResetPasswordRequest])) { request =>
        complete {
          authService.requestPasswordReset(request)
        }
      } ~ (post & pathPrefix("password") & pathPrefix("reset") & pathPrefix("confirm") & pathEnd & entity(as[ConfirmResetPasswordRequest])) { request =>
        complete {
          authService.confirmResetPassword(request)
        }
      } ~ (post & pathPrefix("fb") & pathEnd) {
        entity(as[OAuthRequestWithToken]) { fbAuthRequest =>
          complete {
            authService.fbLogin(fbAuthRequest)
          }
        }
      } ~ (get & pathPrefix("session") & pathEnd & authenticate(authenticator)) { identity =>
        complete {
          OK
        }
      } ~ (delete & pathPrefix("session") & pathEnd & authenticate(authenticator)) { identity =>
        complete {
          authService.logout(identity.token)
          OK
        }
      }
    }
}
