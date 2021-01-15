package org.example.mytestapi

import akka.actor.ActorSystem
import com.typesafe.scalalogging.LazyLogging
import org.example.mytestapi.Model.Book

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object ClientApp extends App with LazyLogging {

  import sttp.client3._
  import sttp.client3.akkahttp._
  import sttp.tapir.client.sttp.SttpClientInterpreter

  val actorSystem: ActorSystem = ActorSystem("client-system")
  val backend = AkkaHttpBackend.usingActorSystem(actorSystem)

  private val future1: Future[Response[Either[String, Book]]] = SttpClientInterpreter
    .toRequestThrowDecodeFailures(Endpoints.getBook, Some(uri"http://localhost:8080"))
    .apply(19)
    .send(backend)

  logger.info("Sending request")
  private val result1: Response[Either[String, Book]] = Await.result(future1, Duration.Inf)
  logger.info("Request received")

  logger.info(s"Status: ${result1.code.code}")
  result1.body match {
    case Left(errorMsg) =>
      logger.error(s"Received error from API: $errorMsg")
    case Right(book) =>
      logger.info(s"book: ${book}")
  }


  private val future2: Future[Response[Either[String, String]]] = SttpClientInterpreter
    .toRequestThrowDecodeFailures(Endpoints.getHello, Some(uri"http://localhost:8080"))
    .apply("Chuck")
    .send(backend)


  logger.info("Sending request")
  private val result2: Response[Either[String, String]] = Await.result(future2, Duration.Inf)
  logger.info("Request received")

  logger.info(s"Status: ${result2.code.code}")
  result2.body match {
    case Left(errorMsg) =>
      logger.error(s"Received error from API: $errorMsg")
    case Right(msg) =>
      logger.info(s"Msg: ${msg}")
  }

  logger.info("Terminating actor system")
  Await.ready(actorSystem.terminate(), Duration.Inf)
  logger.info("Finished")



}
