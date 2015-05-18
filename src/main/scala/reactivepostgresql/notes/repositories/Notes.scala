package reactivepostgresql.notes.repositories

import java.util.UUID

import reactivepostgresql.core.PostgresDriver.api._
import reactivepostgresql.notes.be.Note
import slick.lifted.ProvenShape

object Notes {
  val notes = TableQuery[Notes]
}

class Notes(tag: Tag) extends Table[Note](tag, "notes") {
  def uuid: Rep[UUID] = column[UUID]("uuid")

  def userUuid: Rep[UUID] = column[UUID]("user_uuid")

  def subject: Rep[String] = column[String]("subject")

  def content: Rep[String] = column[String]("content")

  override def * : ProvenShape[Note] = (uuid, userUuid, subject, content) <>(Note.tupled, Note.unapply)
}
