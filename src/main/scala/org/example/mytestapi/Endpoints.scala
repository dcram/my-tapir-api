package org.example.mytestapi

import org.example.mytestapi.Model.Book
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter

object Endpoints {

  import io.circe.generic.auto._
  import sttp.tapir._
  import sttp.tapir.generic.auto._
  import sttp.tapir.json.circe._


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

  val docs = OpenAPIDocsInterpreter.toOpenAPI(List(getHello, getBook), "My Test API", "1.0")


}
