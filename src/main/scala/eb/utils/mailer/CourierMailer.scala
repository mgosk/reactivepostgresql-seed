package eb.utils.mailer

import javax.mail.internet.{MimeBodyPart}
import akka.event.LoggingAdapter
import com.typesafe.config.{Config, ConfigFactory}
import courier.Defaults._
import courier._
import javax.mail.internet.InternetAddress
import twirl.api.Html

object CourierMailer {
  def forConfig(path: String, config: Config = ConfigFactory.load())(implicit logger: LoggingAdapter): CourierMailer = {
    val configInternal = if (path.isEmpty) config else config.getConfig(path)
    new CourierMailer(
      mailerHost = configInternal.getString("host"),
      mailerPort = configInternal.getInt("port"),
      mailerUser = configInternal.getString("user"),
      mailerPassword = configInternal.getString("password"),
      mailerStartTtls = configInternal.getBoolean("startTtls"),
      from = configInternal.getString("from"),
      logger = logger
    )
  }
}

class CourierMailer(mailerHost: String, mailerPort: Int, mailerUser: String, mailerPassword: String, mailerStartTtls: Boolean,
                    from: String, logger: LoggingAdapter) extends Mailer {
  private val courierMailer = Mailer(mailerHost, mailerPort).auth(true).as(mailerUser, mailerPassword).startTtls(mailerStartTtls)()

  override def sendEmail(to: String, subject: String, content: String): Unit = {
    val mailFuture = courierMailer(Envelope.from(new InternetAddress(from)).to(new InternetAddress(to)).subject(subject).content(Text(content)))
    mailFuture.onSuccess { case _ => logger.info(s"Email successfully with subject $subject sent to $to") }
    mailFuture.onFailure { case t: Throwable => logger.error(s"Error while sending email with subject $subject to $to, reason ${t.getMessage}") }
  }

  override def sendWrapped(to: String, subject: String, htmlContent: Html) = {
    val body: String = html.mailTemplate.render(htmlContent).body
    val messageBodyPart = new MimeBodyPart()
    messageBodyPart.setContent(body, "text/html; charset=utf-8")
    val mailFuture = courierMailer(Envelope.from(new InternetAddress(from))
      .to(new InternetAddress(to))
      .subject(subject)
      .content(Multipart().add(messageBodyPart)))
    mailFuture.onSuccess { case _ => logger.info(s"Email successfully with subject $subject sent to $to") }
    mailFuture.onFailure { case t: Throwable => logger.error(s"Error while sending email with subject $subject to $to, reason ${t.getMessage}") }
  }
}



