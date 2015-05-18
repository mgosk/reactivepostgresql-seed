import org.scalastyle.sbt.ScalastylePlugin
import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._
import com.typesafe.sbt.packager.archetypes.ServerLoader.SystemV
import twirl.sbt.TwirlPlugin._

name := "reactivepostgresql-seed"

organization := "reactivepostgresql-seed"

version := "0.0.1"

scalaVersion := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "spray repo" at "http://repo.spray.io/"

resolvers += "Pellucid Bintray" at "http://dl.bintray.com/pellucid/maven"

resolvers += "softprops-maven" at "http://dl.bintray.com/content/softprops/maven"

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.2"
  val sprayJsonV = "1.3.1"
  val slickV = "2.1.0"
  val slickPgV = "0.9.0"
  val jbcryptV = "0.3m"
  val courierV = "0.1.3"
  val sealerateV = "0.0.3"
  Seq(
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-client" % sprayV,
    "io.spray" %% "spray-json" % sprayJsonV,
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.github.tminglei" %% "slick-pg" % slickPgV exclude("org.slf4j", "slf4j-simple"),
    "ch.qos.logback" % "logback-classic" % "1.0.13",
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.zaxxer" % "HikariCP" % "2.3.5",
    "org.mindrot" % "jbcrypt" % jbcryptV,
    "me.lessis" %% "courier" % courierV,
    "com.restfb" % "restfb" % "1.9.0"
  )
}

seq(flywaySettings: _*)

flywayUrl := "jdbc:postgresql://localhost:5432/rp"

flywayUser := "postgres"

flywayPassword := "postgres"

ScalastylePlugin.projectSettings ++
  Seq(ScalastylePlugin.scalastyleConfig := file("project/scalastyle-config.xml"),
    ScalastylePlugin.scalastyleFailOnError := true)

scapegoatIgnoredFiles := Seq(".*/src/main/scala/eb/utils/spray/.*")

Revolver.settings

Revolver.enableDebugging(port = 5005, suspend = false)

javaOptions in Revolver.reStart += "-Xmx2g"

//packager conf

packageArchetype.java_server

maintainer in Linux := "Marcin Gosk <xxx@gmail.com>"

packageSummary in Linux := "reactivepostgresql-seed package summary"

packageDescription := "reactivepostgresql-seed longer package description"

serverLoading in Debian := SystemV

daemonUser in Linux := "reactivepostgresql-seed"

daemonGroup in Linux := "reactivepostgresql-seed"

sources in doc in Compile := List()

//twirl conf

Twirl.settings //twirl conf

net.virtualvoid.sbt.graph.Plugin.graphSettings

