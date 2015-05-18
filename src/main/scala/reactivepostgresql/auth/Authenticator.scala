package reactivepostgresql.auth

import reactivepostgresql.auth.be.{Token, Identity}
import spray.routing.AuthenticationFailedRejection.{CredentialsMissing, CredentialsRejected}
import spray.routing.authentication.{Authentication, ContextAuthenticator}
import spray.routing.{AuthenticationFailedRejection, RequestContext}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class Authenticator(authService: AuthService)(implicit executionContext: ExecutionContext) extends ContextAuthenticator[Identity] {
  private val allowedHeaders = List("x-authtoken", "authtoken")

  override def apply(requestContext: RequestContext): Future[Authentication[Identity]] = {
    requestContext.request.headers.find(header => allowedHeaders.contains(header.name.toLowerCase)) match {
      case Some(token) =>
        Try(Token(token.value)) match {
          case Success(token) =>
            authService.authenticate(token).map[Authentication[Identity]] {
              case Right((user, authToken)) => Right(Identity(user, authToken.token, authToken.validTo))
              case _ => Left(AuthenticationFailedRejection(CredentialsRejected, List()))
            }
          case Failure(x) => Future.successful {
            Left(AuthenticationFailedRejection(CredentialsRejected, List()))
          }
        }
      case _ => Future.successful {
        Left(AuthenticationFailedRejection(CredentialsMissing, List()))
      }
    }
  }
}

