package reactivepostgresql.common

//TODO add support for status codes later
case class ErrorWrapper(code: String, message: String,  detailedMessage: Option[String] = None)
