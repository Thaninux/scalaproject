ThisBuild / scalaVersion := "2.13.14"


lazy val common = (project in file("modules/common"))
  .settings(
    name := "common"
  )



lazy val ingestion = (project in file("modules/ingestion"))
  .settings(
    name := "ingestion",
    libraryDependencies ++= Seq(
      "org.apache.kafka" % "kafka-clients" % "3.6.1",
      "io.circe" %% "circe-core" % "0.14.6",
      "io.circe" %% "circe-generic" % "0.14.6",
      "io.circe" %% "circe-parser" % "0.14.6",
      "com.typesafe.akka" %% "akka-stream" % "2.6.20",
      "com.typesafe.akka" %% "akka-actor-typed" % "2.6.20",
      "com.softwaremill.sttp.client3" %% "core" % "3.8.15",
      "com.softwaremill.sttp.client3" %% "akka-http-backend" % "3.8.15"
    )
  ).dependsOn(common)

lazy val processing = (project in file("modules/processing"))
  .settings(
    name := "processing",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % "2.6.20",
      "com.typesafe.akka" %% "akka-stream-kafka" % "3.0.0",
      "com.typesafe.akka" %% "akka-actor-typed" % "2.6.20",
      "io.circe" %% "circe-core" % "0.14.3",
      "io.circe" %% "circe-generic" % "0.14.3",
      "io.circe" %% "circe-parser" % "0.14.3",
      "io.spray" %% "spray-json" % "1.3.6",
      "org.slf4j" % "slf4j-simple" % "2.0.12"
    )
  ).dependsOn(common)


lazy val root = (project in file("."))
  .aggregate(common, ingestion) 
  .settings(
    name := "weather-pipeline",
    version := "0.1.0"
  )