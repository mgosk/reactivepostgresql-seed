package eb.profiles

import eb.profiles.be.Sex

object ProfileDto {

  case class LocationResponse(latitude: BigDecimal, longitude: BigDecimal, home: Option[String])

  case class NickResponse(nick: String)

  case class SexResponse(sex: Sex)

  case class AgeResponse(age: Int)

  case class WeightResponse(weight: Int)

  case class AvatarResponse(avatar: String)

  case class ProfileUpdateRequest(homeLatitude: Option[BigDecimal] = None,
                                  homeLongitude: Option[BigDecimal] = None,
                                  homeAddress: Option[String] = None,
                                  nick: Option[String] = None,
                                  sex: Option[Sex] = None,
                                  age: Option[Int] = None,
                                  weight: Option[Int] = None,
                                  avatar: Option[String] = None,
                                  height: Option[Int] = None)

}
