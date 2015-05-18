package reactivepostgresql.profiles.be

import java.time.ZonedDateTime
import java.util.UUID

case class Profile(userUuid: UUID,
                   created: ZonedDateTime = ZonedDateTime.now(),
                   homeLatitude: Option[BigDecimal] = None,
                   homeLongitude: Option[BigDecimal] = None,
                   homeAddress: Option[String] = None,
                   nick: Option[String] = None,
                   sex: Option[Sex] = None,
                   age: Option[Int] = None,
                   weight: Option[Int] = None,
                   avatar: Option[String] = None,
                   height: Option[Int] = None)