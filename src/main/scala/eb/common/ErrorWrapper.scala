package eb.common

//add status codes later
//httpCode: StatusCode = BadRequest,
case class ErrorWrapper(code: String, message: String,  detailedMessage: Option[String] = None)
