package eb.auth.services

import java.util.UUID
import java.math.BigInteger
import java.security.SecureRandom
import java.time.ZonedDateTime
import org.mindrot.jbcrypt.BCrypt


case class EmailAddress(address: String) {
  require(EmailAddress.EmailRegex.pattern.matcher(address.toUpperCase).matches(), "Email address has incorrect format")
}

object EmailAddress {
  private val EmailRegex = """\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}\b""".r // NOTE: only uppercase matching
}

case class PasswordHash(hash: String) {
  require(hash.length == PasswordHash.PasswordHashLength, "Password hash has incorrect format")

  def matches(password: Password): Boolean = BCrypt.checkpw(password.password, hash)

  override def toString: String = "PasswordHash(xxx)"
}

case class Password(password: String) {
  require(password.length >= Password.MinimumPasswordLength, "Password must be at least 6 characters long")
}

object Password {
  private val MinimumPasswordLength = 6
}

object PasswordHash {
  def hash(password: Password): PasswordHash = PasswordHash(BCrypt.hashpw(password.password, BCrypt.gensalt(SaltLength)))

  private val PasswordHashLength = 60
  private val SaltLength = 12
}

case class Token(token: String)

object Token {
  def generate: Token = Token(new BigInteger(BitsCount, random).toString(Radix))

  private val random = new SecureRandom()

  private val TokenLength = 51
  private val BitsCount = 255
  private val Radix = 32
}

case class User(uuid:UUID = UUID.randomUUID(),
                emailAddress: EmailAddress,
                passwordHash: Option[PasswordHash] = None,
                created: ZonedDateTime = ZonedDateTime.now(),
                activated:Boolean = false,
                deleted:Boolean = false,
                emailToken: Option[Token] = None,
                emailTokenValidTo: Option[ZonedDateTime] = None,
                facebookId: Option[String] = None)
