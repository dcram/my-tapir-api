package org.example.mytestapi

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.swagger.akkahttp.SwaggerAkka

import scala.io.StdIn

object MyTestApiApp extends App {

  import akka.http.scaladsl.server.Route
  import io.circe.generic.auto._
  import sttp.tapir._
  import sttp.tapir.generic.auto._
  import sttp.tapir.json.circe._
  import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
  import sttp.tapir.openapi.circe.yaml._
  import scala.concurrent.Future


  implicit val system = ActorSystem("my-system")
  implicit val ec = system.dispatcher

  case class Book(author: String, title: String, year: Int)

  val getBook: Endpoint[Int, String, Book, Any] =
    endpoint
      .get
      .in("book" / query[Int]("id"))
      .errorOut(stringBody)
      .out(jsonBody[Book])

  val getHello: Endpoint[String, String, String, Any] =
    endpoint
      .get
      .in("hello" / path[String]("name"))
      .errorOut(stringBody)
      .out(jsonBody[String])

  def hello(name: String): Future[Either[String, String]] =
    Future.successful(Right[String, String](s"Hello ${name} !"))

  def asBook(id: Int): Future[Either[String, Book]] = {
    println(s"Icigo ! $id")
    Future.successful(Right[String, Book](Book(s"Author $id", s"Title $id", id)))
  }

  val docs = OpenAPIDocsInterpreter.toOpenAPI(List(getHello, getBook), "My Test API", "1.0")



  val helloRoute: Route = AkkaHttpServerInterpreter.toRoute(getHello)(hello)
  val bookRoute: Route = AkkaHttpServerInterpreter.toRoute(getBook)(asBook)

  val bindingFuture = Http().newServerAt("localhost", 8080)
    .bind(
      concat(
        helloRoute,
        bookRoute,
        new SwaggerAkka(docs.toYaml).routes
      )
    )

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
