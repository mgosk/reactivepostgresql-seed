package reactivepostgresql.notes.be

import java.util.UUID

case class Note(uuid: UUID = UUID.randomUUID(),
                userUuid: UUID,
                subject: String,
                content: String)
