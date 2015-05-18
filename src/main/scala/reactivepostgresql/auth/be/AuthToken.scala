package reactivepostgresql.auth.be

import java.time.ZonedDateTime
import java.util.UUID

case class AuthToken(token: Token, userUUID: UUID, validTo: ZonedDateTime, prolong: Boolean = false)