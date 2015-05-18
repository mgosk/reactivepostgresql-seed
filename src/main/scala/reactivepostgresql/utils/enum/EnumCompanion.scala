package reactivepostgresql.utils.enum

import java.util.NoSuchElementException

trait EnumBase {
  def name: String
}

trait EnumCompanion[T <: EnumBase] {
  val Values: Set[T]

  def nameToValue(name: String): Option[T] = Values.find(_.name == name)

  def nameToValueUnsafe(name: String): T = nameToValue(name).getOrElse(throw new NoSuchElementException(s"$name should be one of (${Values.mkString(", ")})"))
}
