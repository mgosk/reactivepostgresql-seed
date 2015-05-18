package reactivepostgresql.auth.be

import java.time.ZonedDateTime

case class Identity(user: User, token: Token, validTo: ZonedDateTime)
