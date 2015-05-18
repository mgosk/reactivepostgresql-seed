package reactivepostgresql.profiles.be

case class Location(latitude: BigDecimal, longitude: BigDecimal, address: Option[String])
