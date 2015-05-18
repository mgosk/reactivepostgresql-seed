package reactivepostgresql.config

import com.typesafe.config.{ConfigFactory, Config}

case class FbConfig(appId: String, secret: String)

object FbConfig {
  def load(path: String, config: Config = ConfigFactory.load): FbConfig = {
    val configInternal = if (path.isEmpty) config else config.getConfig(path)
    FbConfig(
      configInternal.getString("appId"),
      configInternal.getString("appSecret")
    )
  }
}