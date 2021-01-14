package org.example.mytestapi

import akka.actor.ActorSystem
import com.typesafe.scalalogging.LazyLogging
import org.example.mytestapi.Model.Book
import sttp.tapir.DecodeResult

object ClientApp extends LazyLogging {

  import sttp.tapir.client.sttp.SttpClientInterpreter
  import sttp.client3._
  import sttp.model._

  import sttp.client3.akkahttp._

  val actorSystem: ActorSystem = ActorSystem("client-system")
  val backend = AkkaHttpBackend.usingActorSystem(actorSystem)

  val makeBookRequest: Request[DecodeResult[Either[String, Book]], Any] = SttpClientInterpreter
    .toRequest(Endpoints.getBook, Some(uri"http://localhost:8080"))
    .apply(19)

  // TODO send the request

}
