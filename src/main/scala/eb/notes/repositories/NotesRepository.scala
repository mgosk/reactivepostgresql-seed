package eb.notes.repositories

import java.util.UUID
import eb.core.PostgresDriver.api._
import eb.notes.be.Note
import eb.notes.repositories.Notes._
import scala.concurrent.{ExecutionContext, Future}

class NotesRepository(db: Database)(implicit ec: ExecutionContext) {

  def findByUserUuid(uuid: UUID): Future[Seq[Note]] = db.run {
    byUserUuidQuery(uuid).result
  }

  def findByUuid(uuid: UUID): Future[Option[Note]] = db.run {
    byUuidQuery(uuid).result.headOption
  }

  def insert(note: Note): Future[Note] = db.run {
    notes += note
  }.map { num => note }

  def update(note: Note): Future[Int] = db.run {
    byUuidQuery(note.uuid).update(note)
  }

  def delete(uuid: UUID): Future[Int] = db.run {
    byUuidQuery(uuid).delete
  }

  private def byUuidQuery(uuid: Rep[UUID]) = notes.filter(_.uuid === uuid)

  private def byUserUuidQuery(uuid: Rep[UUID]) = notes.filter(_.userUuid === uuid)
}
