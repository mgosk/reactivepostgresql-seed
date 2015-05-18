package eb.auth

import eb.auth.services._

case class RegisterRequest(email: EmailAddress, password: Password)

case class ActivationRequest(token: Token)

case class LoginRequest(email: EmailAddress, password: Password)

case class ResetPasswordRequest(email: EmailAddress)

case class ConfirmResetPasswordRequest(token: Token, password: Password)

case class OAuthRequestWithToken(accessToken: String, expiresIn: Long)
