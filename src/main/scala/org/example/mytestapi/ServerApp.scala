package org.example.mytestapi

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import com.typesafe.scalalogging.LazyLogging
import org.example.mytestapi.Logic.{makeBook, sayHello}
import sttp.tapir.swagger.akkahttp.SwaggerAkka

import scala.io.StdIn

object ServerApp extends App with LazyLogging {
  logger.info(s"Starting")

  // Emulates when the endpoint is served behind a reverse-proxy
  private val ContextPath = "mytestapi"

  import Endpoints._
  import akka.http.scaladsl.server.Directives._
  import akka.http.scaladsl.server.Route
  import io.circe.generic.auto._
  import io.circe.syntax._
  import sttp.tapir.openapi.circe.yaml._
  import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

  implicit val system = ActorSystem("my-system")
  implicit val ec = system.dispatcher


  val helloRoute: Route = AkkaHttpServerInterpreter.toRoute(getHello)(sayHello)
  val bookRoute: Route = AkkaHttpServerInterpreter.toRoute(getBook)(makeBook)

  case class Status(uptimeSec: Long, swagger:String)
  val Start = System.currentTimeMillis()

  val status = get { ctx =>
    val u = ctx.request.uri
    ctx.complete(
      HttpEntity(ContentTypes.`application/json`, Status(
        (System.currentTimeMillis() - Start) / 1000,
        s"${u.scheme}://${u.authority}/${ContextPath}/docs"
      ).asJson.spaces2)
    )
  }

  private val routes = concat(
    path("") {
      status
    },
    path("status") {
      status
    },
    helloRoute,
    bookRoute,
    new SwaggerAkka(docs.toYaml).routes
  )

  val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes)

  logger.info(s"Server online at http://localhost:8080/${ContextPath}\nSwagger at http://localhost:8080/${ContextPath}/docs\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

}
