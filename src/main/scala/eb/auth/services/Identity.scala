package eb.auth.services

import java.time.ZonedDateTime

case class Identity(user: User, token: Token, validTo: ZonedDateTime)
