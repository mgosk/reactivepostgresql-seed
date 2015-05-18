package eb.auth.services

import java.time.ZonedDateTime
import java.util.UUID

case class AuthToken(token: Token, userUUID: UUID, validTo: ZonedDateTime, prolong: Boolean = false)