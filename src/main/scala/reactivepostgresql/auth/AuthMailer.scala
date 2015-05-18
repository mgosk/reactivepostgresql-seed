package reactivepostgresql.auth

import reactivepostgresql.auth.be.{Token, EmailAddress}
import reactivepostgresql.utils.mailer.Mailer
import twirl.api.Html

class AuthMailer(mailer: Mailer) {
  def sendActivationEmail(to: EmailAddress, token: Token) = {
    val subject = "Registration in reactivepostgresql-seed"
    val content = Html(
      s"""<p>Thanks for registration in reactivepostgresql-seed<br><br>
         |Before using this service you need to activate account<br><br>
         |Your activation token is: ${token.token}<br><br>
                                                    |To fully activate you account click:
                                                    |<a href=\"http://localhost\">Activate</a></p>
                                                    |""".stripMargin)
    mailer.sendWrapped(to.address, subject, content)
  }

  def sendResetPasswordInstructions(to: EmailAddress) = {
    val subject = "Password reset in reactivepostgresql-seed"
    val content = Html(
      s"""<p>You are already registered in reactivepostgresql-seed.<br><br>
         |If you can't remember password please follow this link and further instructions.<br><br>
         |<a href=\"http://localhost\">Reset password</a></p>""".stripMargin)
    mailer.sendWrapped(to.address, subject, content)
  }

  def sendResetPasswordLink(to: EmailAddress, token: Token) = {
    val subject = "Password reset in reactivepostgresql-seed"
    val content = Html(
      s"""<p>Your password rest token is: ${token.token}<br><br>
                                                          |To confirm password reset click:
                                                          |<a href=\"http://localhost\">Reset password</a></p>
                                                          |""".stripMargin)
    mailer.sendWrapped(to.address, subject, content)
  }
}
