package eb.notes

import eb.auth.Authenticator
import eb.common.CommonJsonProtocol
import eb.notes.be.Note
import spray.httpx.SprayJsonSupport._
import spray.routing.Directives._
import scala.concurrent.ExecutionContext
import eb.common.ResponseWrapper._

object NotesRouter extends CommonJsonProtocol {
  implicit val noteProtocol = jsonFormat4(Note)
  implicit val notesListProtocol = jsonFormat1(NotesList)
  implicit val noteRequestProtocol = jsonFormat2(NoteRequest)
}

class NotesRouter(notesService: NotesService, authenticator: Authenticator)(implicit ec: ExecutionContext) {

  import NotesRouter._

  val route =
    (pathPrefix("notes") & authenticate(authenticator)) { implicit identity =>
      (get & pathEnd) {
        complete {
          notesService.getAll()
        }
      } ~ (get & path(JavaUUID) & pathEnd) { uuid =>
        complete {
          notesService.get(uuid)
        }
      } ~ (post & pathEnd) {
        entity(as[NoteRequest]) { request =>
          complete {
            notesService.create(request)
          }
        }
      } ~ (patch & path(JavaUUID) & pathEnd) { uuid =>
        entity(as[NoteRequest]) { request =>
          complete {
            notesService.update(uuid, request)
          }
        }
      } ~ (delete & path(JavaUUID) & pathEnd) { uuid =>
        path(JavaUUID) { uuid =>
          complete {
            notesService.delete(uuid)
          }
        }
      }
    }
}