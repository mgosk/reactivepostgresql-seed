package eb.profiles.repositories

import java.util.UUID
import eb.core.PostgresDriver.api._
import java.time.ZonedDateTime
import eb.profiles.be.{Sex, Profile}
import slick.lifted.ProvenShape

object Profiles {
  val profiles = TableQuery[Profiles]
}

class Profiles(tag: Tag) extends Table[Profile](tag, "profile") {

  import Profiles._

  def uuid: Rep[UUID] = column[UUID]("user_uuid")

  def created: Rep[ZonedDateTime] = column[ZonedDateTime]("created")

  def homeLatitude: Rep[Option[BigDecimal]] = column[Option[BigDecimal]]("home_latitude")

  def homeLongitude: Rep[Option[BigDecimal]] = column[Option[BigDecimal]]("home_longitude")

  def homeAddress: Rep[Option[String]] = column[Option[String]]("home_address")

  def nick: Rep[Option[String]] = column[Option[String]]("nick")

  def sex: Rep[Option[Sex]] = column[Option[Sex]]("sex")

  def age: Rep[Option[Int]] = column[Option[Int]]("age")

  def weight: Rep[Option[Int]] = column[Option[Int]]("weight")

  def avatar: Rep[Option[String]] = column[Option[String]]("avatar")

  def height: Rep[Option[Int]] = column[Option[Int]]("height")

  override def * : ProvenShape[Profile] = (uuid, created, homeLatitude, homeLongitude, homeAddress, nick, sex, age, weight, avatar, height) <>(Profile.tupled, Profile.unapply)
}

