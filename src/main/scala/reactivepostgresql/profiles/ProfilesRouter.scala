package reactivepostgresql.profiles

import reactivepostgresql.auth.Authenticator
import reactivepostgresql.common.CommonJsonProtocol
import reactivepostgresql.profiles.ProfileDto._
import reactivepostgresql.profiles.be.{Sex, Location, Profile}
import spray.httpx.SprayJsonSupport._
import spray.routing.Directives._
import scala.concurrent.ExecutionContext
import reactivepostgresql.common.ResponseWrapper._

object ProfilesRouter extends CommonJsonProtocol {
  implicit val documentTypeFormat = fromStringJsonFormat[Sex]({ s: String => Sex(s) }, { d: Sex => d.toString })
  implicit val location = jsonFormat3(Location)
  implicit val locationR = jsonFormat3(LocationResponse)
  implicit val profile = jsonFormat11(Profile)
  implicit val profileUpdateRequest = jsonFormat9(ProfileUpdateRequest)
  implicit val nickResponse = jsonFormat1(NickResponse)
  implicit val sexResponse = jsonFormat1(SexResponse)
  implicit val ageResponse = jsonFormat1(AgeResponse)
  implicit val weightResponse = jsonFormat1(WeightResponse)
  implicit val avatarResponse = jsonFormat1(AvatarResponse)
}

class ProfilesRouter(profileService: ProfilesService, authenticator: Authenticator)(implicit ec: ExecutionContext) {

  import ProfilesRouter._

  val route =
    (pathPrefix("profiles") & authenticate(authenticator)) { implicit identity =>
      (get & path("me") & pathEnd) {
        complete {
          profileService.getProfile
        }
      } ~ (get & path("me" / "home") & pathEnd) {
        complete {
          profileService.getHome
        }
      } ~ (get & path("me" / "nick") & pathEnd) {
        complete {
          profileService.getNick
        }
      } ~ (get & path("me" / "sex") & pathEnd) {
        complete {
          profileService.getSex
        }
      } ~ (get & path("me" / "age") & pathEnd) {
        complete {
          profileService.getAge
        }
      } ~ (get & path("me" / "weight") & pathEnd) {
        complete {
          profileService.getWeight
        }
      } ~ (get & path("me" / "avatar") & pathEnd) {
        complete {
          profileService.getAvatar
        }
      } ~ (post & path("me" / "home") & pathEnd & pathEnd) {
        entity(as[Location]) { location =>
          complete {
            profileService.saveHome(location)
          }
        }
      } ~ (put & path("me") & pathEnd & pathEnd) {
        entity(as[ProfileUpdateRequest]) { profileRequest =>
          complete {
            profileService.updateProfile(profileRequest)
          }
        }
      }
    }
}
