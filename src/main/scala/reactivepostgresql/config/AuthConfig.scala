package reactivepostgresql.config

import java.time.Duration

import com.typesafe.config.{ConfigFactory, Config}

case class AuthConfig(sessionLongTtl: Duration, sessionShortTtl: Duration, emailTokenTtl: Duration)

object AuthConfig {
  def load(path: String, config: Config = ConfigFactory.load): AuthConfig = {
    val configInternal = if (path.isEmpty) config else config.getConfig(path)
    AuthConfig(
      Duration.ofMinutes(configInternal.getLong("session.longTtl")),
      Duration.ofMinutes(configInternal.getLong("session.shortTtl")),
      Duration.ofMinutes(configInternal.getLong("emailTokenTtl"))
    )
  }
}
