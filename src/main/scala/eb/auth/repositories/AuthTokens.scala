package eb.auth.repositories

import java.util.UUID

import eb.auth.services.{Token, AuthToken}
import eb.core.PostgresDriver.api._
import java.time.ZonedDateTime
import slick.lifted.ProvenShape

class AuthTokens(tag: Tag) extends Table[AuthToken](tag, "auth_token") {

  import Users._

  def value: Rep[Token] = column[Token]("value")

  def userUuid: Rep[UUID] = column[UUID]("user_uuid")

  def validTo: Rep[ZonedDateTime] = column[ZonedDateTime]("valid_to")

  def prolong: Rep[Boolean] = column[Boolean]("prolong")

  override def * : ProvenShape[AuthToken] = (value, userUuid, validTo, prolong) <>(AuthToken.tupled, AuthToken.unapply)
}

object AuthTokens {
  val authTokens = TableQuery[AuthTokens]
}
