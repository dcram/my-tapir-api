package org.example.mytestapi

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.concat
import com.typesafe.scalalogging.LazyLogging
import org.example.mytestapi.Logic.{makeBook, sayHello}
import sttp.tapir.swagger.akkahttp.SwaggerAkka

import scala.io.StdIn

object ServerApp extends App with LazyLogging {
  logger.info(s"Starting")

  import Endpoints._
  import akka.http.scaladsl.server.Route
  import sttp.tapir.openapi.circe.yaml._
  import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter


  implicit val system = ActorSystem("my-system")
  implicit val ec = system.dispatcher


  val helloRoute: Route = AkkaHttpServerInterpreter.toRoute(getHello)(sayHello)
  val bookRoute: Route = AkkaHttpServerInterpreter.toRoute(getBook)(makeBook)

  val bindingFuture = Http().newServerAt("localhost", 8080)
    .bind(
      concat(
        helloRoute,
        bookRoute,
        new SwaggerAkka(docs.toYaml).routes
      )
    )

  logger.info(s"Server online at http://localhost:8080/\nSwagger at http://localhost:8080/docs\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

}
