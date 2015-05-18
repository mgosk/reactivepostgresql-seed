package reactivepostgresql.core

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import spray.can.Http
import spray.routing.SimpleRoutingApp

object Main extends App with SimpleRoutingApp {
  implicit val actorSystem = ActorSystem("spray-can")
  implicit val timeout = Timeout(5.seconds)

  val config = ConfigFactory.load()
  val globalRoutingActor = actorSystem.actorOf(Props[GlobalRoutingActor], "global-routing-actor")
  val interface = config.getString("http.interface")
  val port = config.getInt("http.port")

  IO(Http) ? Http.Bind(globalRoutingActor, interface, port)
}
