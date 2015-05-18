package eb.profiles.repositories

import java.util.UUID
import eb.core.PostgresDriver.api._
import Profiles._
import eb.profiles.be.Profile
import scala.concurrent.{Future, ExecutionContext}

class ProfileRepository(db: Database)(implicit ec: ExecutionContext) {

  def findByUserUuid(uuid: UUID): Future[Option[Profile]] = db.run {
    byUserUuidQueryCompiled(uuid).result.headOption
  }

  def insert(profile: Profile): Future[Profile] = db.run {
    profiles += profile
  }.map { num => profile }

  def update(profile: Profile): Future[Int] = db.run {
    byUserUuidQueryCompiled(profile.userUuid).update(profile)
  }

  private def byUserUuidQuery(uuid: Rep[UUID]) = profiles.filter(_.uuid === uuid)

  private val byUserUuidQueryCompiled = Compiled(byUserUuidQuery _)

}
