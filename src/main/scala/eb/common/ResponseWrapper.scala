package eb.common

import spray.http._
import spray.http.StatusCodes._
import spray.httpx.marshalling.{Marshaller, ToResponseMarshaller}
import spray.json.DefaultJsonProtocol

import scala.concurrent.{ExecutionContext, Future}

object ResponseWrapper extends DefaultJsonProtocol {
  type ResponseWrapper[B] = Future[Either[ErrorWrapper, B]]

  //TODO optimize IT
  //TODO add support for difeerent status codes
  implicit def responseWrapperMarshaller[B](implicit mb: ToResponseMarshaller[B], ec: ExecutionContext) =
    ToResponseMarshaller.of[ResponseWrapper[B]](ContentTypes.`application/json`) { (value, contentType, ctx) =>
      value.map {
        case Left(l: ErrorWrapper) =>
          val detailedMessage = if (l.detailedMessage.isDefined) s""","detailedMessage":"${l.detailedMessage}" """ else ""
          val errorJson = s"""{"code": "${l.code}","message": "${l.message}"${detailedMessage}"""
          ctx.marshalTo(HttpResponse(BadRequest, HttpEntity(MediaTypes.`application/json`, HttpData(errorJson))))
        case Right(r) => mb(r, ctx)
      }
    }
}
