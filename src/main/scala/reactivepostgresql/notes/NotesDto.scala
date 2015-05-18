package reactivepostgresql.notes

import reactivepostgresql.notes.be.Note

case class NotesList(data: Seq[Note])

case class NoteRequest(subject: String, content: String)