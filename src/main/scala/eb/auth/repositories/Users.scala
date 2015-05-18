package eb.auth.repositories

import java.util.UUID

import eb.auth.services._
import eb.core.PostgresDriver.api._
import java.time.ZonedDateTime
import slick.lifted.ProvenShape

object Users {
  val users = TableQuery[Users]
  implicit val emailAddressColumnType = MappedColumnType.base[EmailAddress, String]({ ea => ea.address }, { s => EmailAddress(s) })
  implicit val passwordHashColumnType = MappedColumnType.base[PasswordHash, String]({ ph => ph.hash }, { s => PasswordHash(s) })
  implicit val tokenColumnType = MappedColumnType.base[Token, String]({ t => t.token }, { s => Token(s) })
}

class Users(tag: Tag) extends Table[User](tag, "user") {

  import Users._

  def uuid: Rep[UUID] = column[UUID]("uuid")

  def emailAddress: Rep[EmailAddress] = column[EmailAddress]("email_address")

  def passwordHash: Rep[Option[PasswordHash]] = column[Option[PasswordHash]]("password_hash")

  def created: Rep[ZonedDateTime] = column[ZonedDateTime]("created")

  def activated: Rep[Boolean] = column[Boolean]("activated")

  def deleted: Rep[Boolean] = column[Boolean]("deleted")

  def emailToken: Rep[Option[Token]] = column[Option[Token]]("email_token")

  def emailTokenValidTo: Rep[Option[ZonedDateTime]] = column[Option[ZonedDateTime]]("email_token_valid_to")

  def facebookId: Rep[Option[String]] = column[Option[String]]("facebook_id")

  override def * : ProvenShape[User] = (uuid, emailAddress, passwordHash, created, activated, deleted, emailToken, emailTokenValidTo, facebookId) <>(User.tupled, User.unapply)
}