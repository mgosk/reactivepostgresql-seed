resolvers += "Flyway" at "http://flywaydb.org/repo"

resolvers += "spray repo" at "http://repo.spray.io"

addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2")

addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "0.94.6")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.6.0")

addSbtPlugin("com.orrsella" % "sbt-stats" % "1.0.5")

addSbtPlugin("org.flywaydb" % "flyway-sbt" % "3.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "0.8.0-RC1")

addSbtPlugin("io.spray" % "sbt-twirl" % "0.7.0")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.5")

