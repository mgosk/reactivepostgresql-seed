package eb.core

import akka.event.Logging._
import akka.event.{Logging, LoggingAdapter}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import eb.auth.repositories.{AuthTokenRepository, UserRepository}
import eb.auth.services.AuthService
import eb.auth.{AuthMailer, AuthenticationRouter, Authenticator}
import eb.config.{AuthConfig, FbConfig}
import eb.core.PostgresDriver.api._
import eb.profiles.repositories.ProfileRepository
import eb.profiles.{ProfilesRouter, ProfilesService}
import eb.utils.mailer.{Mailer, CourierMailer}
import eb.notes.repositories.NotesRepository
import eb.notes.{NotesRouter, NotesService}
import spray.routing.{ExceptionHandler, HttpServiceActor, RejectionHandler, RoutingSettings}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


class GlobalRoutingActor extends HttpServiceActor {
  implicit val routingSettings: RoutingSettings = RoutingSettings.default(actorRefFactory)
  implicit val logger: LoggingAdapter = Logging(this)
  implicit val exceptionHandler: ExceptionHandler = ExceptionHandler.default
  implicit val rejectionHandler: RejectionHandler = RejectionHandler.Default
  implicit val executionContext: ExecutionContext = context.dispatcher
  implicit val requestTimeout = Timeout(60 seconds)
  implicit val actorSystem = context.system

  override def receive: Receive = runRoute(route)

  val config = ConfigFactory.load()
  val db = Database.forConfig("db", config)
  val mailer: Mailer = CourierMailer.forConfig("mailer", config)
  val fbConfig = FbConfig.load("auth.fb", config)
  val authConfig = AuthConfig.load("auth", config)

  val userRepository = new UserRepository(db)
  val authTokenRepository = new AuthTokenRepository(db)
  val profileRepository = new ProfileRepository(db)
  val profileService = new ProfilesService(profileRepository)

  val authMailer = new AuthMailer(mailer)
  val authService = new AuthService(userRepository, authTokenRepository, authMailer, authConfig, logger, fbConfig, profileService)

  val authenticator = new Authenticator(authService)
  val authenticationRouter = new AuthenticationRouter(authService, authenticator)

  val profilesRouter = new ProfilesRouter(profileService, authenticator)

  val notesRepository = new NotesRepository(db)
  val notesService = new NotesService(notesRepository)
  val noteRouter = new NotesRouter(notesService, authenticator)

  val route = logRequestResponse("reactivepostgresql-seed", InfoLevel) {
    authenticationRouter.route ~
      profilesRouter.route ~
      noteRouter.route
  }
}


