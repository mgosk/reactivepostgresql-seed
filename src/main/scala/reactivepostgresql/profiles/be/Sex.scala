package reactivepostgresql.profiles.be

import reactivepostgresql.core.PostgresDriver.api._

sealed trait Sex {
  override def toString = Sex.toString(this)
}

object Sex {
  case object Male extends Sex
  case object Female extends Sex
  val mapping: Map[Sex, String] = Map(Male -> "male", Female -> "female")
  val reverseMapping = mapping.map(_.swap)

  def apply(s: String): Sex = {
    reverseMapping.getOrElse(s, throw new IllegalArgumentException(s"Sex state should be one of ${reverseMapping.keys.mkString(", ")}"))
  }

  def toString(vr: Sex): String = {
    mapping.getOrElse(vr, throw new IllegalArgumentException(s"Sex state should be one of ${mapping.keys.mkString(", ")}"))
  }

  implicit val sexFormat = MappedColumnType.base[Sex, String] (
  { vr => toString(vr)},
  { s => apply(s)}
  )

}