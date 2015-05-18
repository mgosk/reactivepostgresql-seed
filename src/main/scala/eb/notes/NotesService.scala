package eb.notes

import java.util.UUID
import eb.auth.services.Identity
import eb.common.ErrorWrapper
import eb.common.ResponseWrapper._
import eb.notes.be.Note
import eb.notes.repositories.NotesRepository
import scala.concurrent.ExecutionContext

class NotesService(notesRepository: NotesRepository)(implicit ec: ExecutionContext) {

  def getAll()(implicit identity: Identity): ResponseWrapper[NotesList] =
    notesRepository.findByUserUuid(identity.user.uuid).map { data =>
      Right(NotesList(data = data))
    }

  def get(uuid: UUID): ResponseWrapper[Note] =
    notesRepository.findByUuid(uuid).map {
      case Some(wingman) =>
        Right(wingman)
      case None =>
        Left(ErrorWrapper("notFound", s"Note with UUID:${uuid} not found"))
    }

  def create(request: NoteRequest)(implicit identity: Identity): ResponseWrapper[Note] =
    notesRepository.insert(
      Note(userUuid = identity.user.uuid,
        subject = request.subject,
        content = request.content)).map {
      Right(_)
    }

  def update(uuid: UUID, request: NoteRequest): ResponseWrapper[Note] =
    notesRepository.findByUuid(uuid).map {
      case Some(note) =>
        val hydrated = hydrate(note, request)
        notesRepository.update(hydrated)
        Right(hydrated)
      case None =>
        Left(ErrorWrapper("notFound", s"Note with UUID:${uuid} not found"))
    }

  def delete(uuid: UUID): ResponseWrapper[String] =
    notesRepository.delete(uuid).map {
      case 1 =>
        Right("deleted")
      case _ =>
        Left(ErrorWrapper("notFound", s"Note with UUID:${uuid} not found"))
    }

  private def hydrate(note: Note, request: NoteRequest): Note = {
    note.copy(
      subject = request.subject,
      content = request.content)
  }
}
