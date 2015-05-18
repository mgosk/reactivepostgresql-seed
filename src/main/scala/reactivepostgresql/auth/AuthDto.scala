package reactivepostgresql.auth

import reactivepostgresql.auth.be.{Token, Password, EmailAddress}

case class RegisterRequest(email: EmailAddress, password: Password)

case class ActivationRequest(token: Token)

case class LoginRequest(email: EmailAddress, password: Password)

case class ResetPasswordRequest(email: EmailAddress)

case class ConfirmResetPasswordRequest(token: Token, password: Password)

case class OAuthRequestWithToken(accessToken: String, expiresIn: Long)
