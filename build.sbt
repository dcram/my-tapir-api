import Dependencies._

ThisBuild / scalaVersion     := "2.12.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "org.example"
ThisBuild / organizationName := "mytestapi"

val tapirVersion = "0.17.4"
val circeVersion = "0.12.3"

lazy val root = (project in file("."))
  .settings(
    name := "My Test API",
    libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
    libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
    libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % tapirVersion,
    libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirVersion,
    libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion,
    libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http" % tapirVersion,
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion),
    libraryDependencies += scalaTest % Test
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
