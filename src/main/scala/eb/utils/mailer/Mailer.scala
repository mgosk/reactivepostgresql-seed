package eb.utils.mailer

import twirl.api.Html

trait Mailer {

  def sendEmail(to: String, subject: String, content: String): Unit

  def sendWrapped(to: String, subject: String, htmlContent: Html): Unit

}
