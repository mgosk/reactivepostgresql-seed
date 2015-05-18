package reactivepostgresql.auth.repositories

import reactivepostgresql.auth.be.{Token, User, AuthToken}
import reactivepostgresql.core.PostgresDriver.api._
import Users._
import AuthTokens._
import scala.concurrent.{Future, ExecutionContext}

class AuthTokenRepository(db: Database)(implicit ec: ExecutionContext) {

  def findByValueWithUser(token: Token): Future[Option[(AuthToken, User)]] = db.run {
    joinedWithUsersQuery(token).result.headOption
  }

  def insert(authToken: AuthToken): Future[AuthToken] = db.run {
    authTokens += authToken
  }.map { num =>
    authToken
  }

  def update(authToken: AuthToken): Future[Int] = db.run {
    byValueQuery(authToken.token).update(authToken)
  }

  def deleteByValue(token: Token): Future[Int] = db.run {
    byValueQuery(token).delete
  }

  def byValueQuery(token: Rep[Token]) = authTokens.filter(_.value === token)

  def joinedWithUsersQuery(token: Rep[Token]) = for {
    authToken <- authTokens
    user <- users
    if user.uuid === authToken.userUuid
    if authToken.value === token
  } yield (authToken, user)

}