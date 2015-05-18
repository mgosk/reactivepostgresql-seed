package reactivepostgresql.profiles

import java.util.UUID
import reactivepostgresql.auth.be.Identity
import reactivepostgresql.common.ErrorWrapper
import reactivepostgresql.common.ResponseWrapper.ResponseWrapper
import reactivepostgresql.profiles.ProfileDto._
import reactivepostgresql.profiles.be.{Location, Profile}
import reactivepostgresql.profiles.repositories.ProfileRepository
import scala.concurrent.{Future, ExecutionContext}


class ProfilesService(profileRepository: ProfileRepository)(implicit ec: ExecutionContext) {

  def getProfile()(implicit identity: Identity): ResponseWrapper[Profile] = {
    profileRepository.findByUserUuid(identity.user.uuid).flatMap {
      case Some(profile) =>
        Future.successful(Right(profile))
      case None =>
        profileRepository.insert(Profile(userUuid = identity.user.uuid)).map { d =>
          Right(d)
        }
    }
  }

  def getHome()(implicit identity: Identity): ResponseWrapper[Location] =
    profileRepository.findByUserUuid(identity.user.uuid).map {
      case Some(profile) =>
        if (profile.homeLatitude.isDefined && profile.homeLongitude.isDefined)
          Right(Location(profile.homeLatitude.getOrElse(throw new NoSuchElementException), profile.homeLongitude.getOrElse(throw new NoSuchElementException), profile.homeAddress)
          )
        else
          Left(ErrorWrapper("locationNotDefined", "Please define your location"))
      case None => profileRepository.insert(Profile(userUuid = identity.user.uuid))
        Left(ErrorWrapper("locationNotDefined", "Please define your location"))
    }


  def getNick()(implicit identity: Identity): ResponseWrapper[NickResponse] =
    profileRepository.findByUserUuid(identity.user.uuid).map {
      case Some(profile) =>
        profile.nick match {
          case Some(nick) => Right(NickResponse(nick))
          case None => Left(ErrorWrapper("nickNotDefined", "Please define your nick"))
        }
      case None => profileRepository.insert(Profile(userUuid = identity.user.uuid))
        Left(ErrorWrapper("nickNotDefined", "Please define your nick"))
    }


  def getSex()(implicit identity: Identity): ResponseWrapper[SexResponse] =
    profileRepository.findByUserUuid(identity.user.uuid).map {
      case Some(profile) =>
        profile.sex match {
          case Some(sex) => Right(SexResponse(sex))
          case None => Left(ErrorWrapper("sexNotDefined", "Please define your sex"))
        }
      case None => profileRepository.insert(Profile(userUuid = identity.user.uuid))
        Left(ErrorWrapper("sexNotDefined", "Please define your sex"))
    }


  def getAge()(implicit identity: Identity): ResponseWrapper[AgeResponse] =
    profileRepository.findByUserUuid(identity.user.uuid).map {
      case Some(profile) =>
        profile.age match {
          case Some(age) => Right(AgeResponse(age))
          case None => Left(ErrorWrapper("ageNotDefined", "Please define your age"))
        }
      case None => profileRepository.insert(Profile(userUuid = identity.user.uuid))
        Left(ErrorWrapper("ageNotDefined", "Please define your age"))
    }


  def getWeight()(implicit identity: Identity): ResponseWrapper[WeightResponse] =
    profileRepository.findByUserUuid(identity.user.uuid).map {
      case Some(profile) =>
        profile.weight match {
          case Some(weight) => Right(WeightResponse(weight))
          case None => Left(ErrorWrapper("weightNotDefined", "Please define your weight"))
        }
      case None => profileRepository.insert(Profile(userUuid = identity.user.uuid))
        Left(ErrorWrapper("weightNotDefined", "Please define your weight"))
    }


  def getAvatar()(implicit identity: Identity): ResponseWrapper[AvatarResponse] =
    profileRepository.findByUserUuid(identity.user.uuid).map {
      case Some(profile) =>
        profile.avatar match {
          case Some(avatar) => Right(AvatarResponse(avatar))
          case None => Left(ErrorWrapper("avatarNotDefined", "Please define your avatar"))
        }
      case None => profileRepository.insert(Profile(userUuid = identity.user.uuid))
        Left(ErrorWrapper("avatarNotDefined", "Please define your avatar"))
    }


  def saveHome(location: Location)(implicit identity: Identity): ResponseWrapper[LocationResponse] = {
    profileRepository.findByUserUuid(identity.user.uuid).map {
      case Some(profile) =>
        profileRepository.update(profile.copy(homeLatitude = Some(location.latitude), homeLongitude = Some(location.longitude)))
        Right(LocationResponse(location.latitude, location.longitude, location.address))
      case None =>
        profileRepository.insert(Profile(userUuid = identity.user.uuid, homeLatitude = Some(location.latitude),
          homeLongitude = Some(location.latitude)))
        Right(LocationResponse(location.latitude, location.longitude, location.address))
    }
  }

  def updateProfile(profileRequest: ProfileUpdateRequest)(implicit identity: Identity): ResponseWrapper[Profile] =
    profileRepository.findByUserUuid(identity.user.uuid).flatMap {
      case Some(profile) =>
        val updated = hydrate(profile, profileRequest)
        profileRepository.update(updated)
        Future.successful(Right(updated))
      case None =>
        profileRepository.insert(
          Profile(userUuid = identity.user.uuid,
            homeLatitude = profileRequest.homeLatitude,
            homeLongitude = profileRequest.homeLongitude,
            homeAddress = profileRequest.homeAddress,
            nick = profileRequest.nick,
            sex = profileRequest.sex,
            age = profileRequest.age,
            weight = profileRequest.weight,
            avatar = profileRequest.avatar)).map {
          Right(_)
        }
    }


  def updateAnonymous(userUuid: UUID, profileRequest: ProfileUpdateRequest): Future[Profile] =
    profileRepository.findByUserUuid(userUuid).flatMap {
      case Some(profile) =>
        val updated = hydrate(profile, profileRequest)
        profileRepository.update(updated)
        Future.successful(updated)
      case None =>
        profileRepository.insert(
          Profile(userUuid = userUuid,
            homeLatitude = profileRequest.homeLatitude,
            homeLongitude = profileRequest.homeLongitude,
            homeAddress = profileRequest.homeAddress,
            nick = profileRequest.nick,
            sex = profileRequest.sex,
            age = profileRequest.age,
            weight = profileRequest.weight,
            avatar = profileRequest.avatar))
    }


  private def hydrate(oldProfile: Profile, profileRequest: ProfileUpdateRequest): Profile = {
    oldProfile.copy(
      homeLatitude = profileRequest.homeLatitude.map(Some(_)).getOrElse(oldProfile.homeLatitude),
      homeLongitude = profileRequest.homeLongitude.map(Some(_)).getOrElse(oldProfile.homeLongitude),
      homeAddress = profileRequest.homeAddress.map(Some(_)).getOrElse(oldProfile.homeAddress),
      nick = profileRequest.nick.map(Some(_)).getOrElse(oldProfile.nick),
      sex = profileRequest.sex.map(Some(_)).getOrElse(oldProfile.sex),
      age = profileRequest.age.map(Some(_)).getOrElse(oldProfile.age),
      weight = profileRequest.weight.map(Some(_)).getOrElse(oldProfile.weight),
      avatar = profileRequest.avatar.map(Some(_)).getOrElse(oldProfile.avatar),
      height = profileRequest.height.map(Some(_)).getOrElse(oldProfile.height)
    )
  }

}
