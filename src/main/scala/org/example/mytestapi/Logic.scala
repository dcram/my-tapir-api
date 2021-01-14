package org.example.mytestapi

import com.typesafe.scalalogging.LazyLogging
import org.example.mytestapi.Model.Book

import scala.concurrent.Future

object Logic extends LazyLogging {

  def sayHello(name: String): Future[Either[String, String]] = {
    logger.info(s"Saying hello !")
    Future.successful(Right[String, String](s"Hello ${name} !"))
  }

  def makeBook(id: Int): Future[Either[String, Book]] = {
    logger.info(s"Making book from id $id")
    Future.successful(Right[String, Book](Book(s"Author $id", s"Title $id", id)))
  }
}
