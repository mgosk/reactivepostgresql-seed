package eb.common

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import eb.utils.enum.{EnumCompanion, EnumBase}
import spray.json._
import scala.util.Try

trait CommonJsonProtocol extends DefaultJsonProtocol {
  implicit val errorWrapper = jsonFormat3(ErrorWrapper.apply)

  def fromStringJsonFormat[T](factory: String => T, extractor: T => String): RootJsonFormat[T] = new RootJsonFormat[T] {
    override def read(json: JsValue): T = json match {
      case JsString(value) => factory(value)
      case _ => deserializationError("String type expected")
    }

    override def write(value: T): JsValue = {
      JsString(extractor(value))
    }
  }

  def jsonFlatFormat[P, T <: Product](construct: P => T)(implicit jw: JsonWriter[P], jr: JsonReader[P]): JsonFormat[T] = new RootJsonFormat[T] {
    override def read(json: JsValue): T = construct(jr.read(json))

    override def write(obj: T): JsValue = jw.write(obj.productElement(0).asInstanceOf[P])
  }

  def jsonEnumFormat[T <: EnumBase](enumCompanion: EnumCompanion[T]) = new JsonFormat[T] {
    override def read(json: JsValue): T = json match {
      case JsString(name) => enumCompanion.nameToValue(name).getOrElse(deserializationError(s"$name should be one of (${enumCompanion.Values.mkString(", ")})"))
      case _ => deserializationError(s"${json.toString()} should be a string of value (${enumCompanion.Values.mkString(", ")})")
    }

    override def write(obj: T): JsValue = JsString(obj.name)
  }

  implicit val zonedDateTimeJsonFormat = new RootJsonFormat[ZonedDateTime] {
    override def read(json: JsValue): ZonedDateTime = json match {
      case JsString(dateTimeString) => Try(ZonedDateTime.parse(dateTimeString, formatter)).getOrElse(deserializationError(deserializationErrorMessage))
      case _ => deserializationError(deserializationErrorMessage)
    }

    override def write(dateTime: ZonedDateTime): JsValue = JsString(dateTime.format(formatter))

    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    private val deserializationErrorMessage = s"Expected date time in ISO offset date time format ex. ${ZonedDateTime.now().format(formatter)}"
  }

  implicit val uuidJsonFormat = new RootJsonFormat[UUID] {
    override def read(json: JsValue): UUID = json match {
      case JsString(uuidString) => Try(UUID.fromString(uuidString)).getOrElse(deserializationError(deserializationErrorMessage))
      case _ => deserializationError(deserializationErrorMessage)
    }

    override def write(uuid: UUID): JsValue = JsString(uuid.toString)

    private val deserializationErrorMessage = "Invalid uuid format"
  }

}
