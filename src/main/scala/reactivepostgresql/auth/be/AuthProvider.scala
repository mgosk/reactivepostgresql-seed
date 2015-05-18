package reactivepostgresql.auth.be

sealed trait AuthProvider

object AuthProvider {

  case object Facebook extends AuthProvider

  case object EmailPassword extends AuthProvider

}
