package eb.auth.repositories

import eb.auth.services.{Token, EmailAddress, User}
import eb.core.PostgresDriver.api._
import Users._
import scala.concurrent.{ExecutionContext, Future}

class UserRepository(db: Database)(implicit ec: ExecutionContext) {
  def findByEmailAddress(emailAddress: EmailAddress): Future[Option[User]] = db.run {
    byEmailAddressCompiled(emailAddress).result.headOption
  }

  def findByEmailToken(emailToken: Token): Future[Option[User]] = db.run {
    byEmailTokenCompiled(emailToken).result.headOption
  }

  def findByFbId(fbId: String): Future[Option[User]] = db.run {
    byFbIdCompiled(fbId).result.headOption
  }

  def insert(user: User): Future[User] = db.run {
    users += user
  }.map { num =>
    user
  }

  def update(user: User): Future[Int] = db.run {
    byEmailAddressQuery(user.emailAddress).update(user)
  }

  def deleteByEmailAddress(emailAddress: EmailAddress): Future[Int] = db.run {
    byEmailAddressQuery(emailAddress).delete
  }

  private def byEmailAddressQuery(emailAddress: Rep[EmailAddress]) = users.filter(_.emailAddress === emailAddress)

  private val byEmailAddressCompiled = Compiled(byEmailAddressQuery _)

  private def byEmailTokenQuery(token: Rep[Token]) = users.filter(_.emailToken === token)

  private val byEmailTokenCompiled = Compiled(byEmailTokenQuery _)

  private def byFbIdQuery(fbId: Rep[String]) = users.filter(_.facebookId === fbId)

  private val byFbIdCompiled = Compiled(byFbIdQuery _)

}