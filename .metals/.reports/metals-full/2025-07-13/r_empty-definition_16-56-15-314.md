error id: file:///C:/Users/ninag/OneDrive/Documents/M1/scala_project/build.sbt:`<none>`.
file:///C:/Users/ninag/OneDrive/Documents/M1/scala_project/build.sbt
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -anomalyDetection.
	 -anomalyDetection#
	 -anomalyDetection().
	 -scala/Predef.anomalyDetection.
	 -scala/Predef.anomalyDetection#
	 -scala/Predef.anomalyDetection().
offset: 497
uri: file:///C:/Users/ninag/OneDrive/Documents/M1/scala_project/build.sbt
text:
```scala
ThisBuild / scalaVersion := "2.13.14"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.6.20",
  "io.circe" %% "circe-core" % "0.14.1",
  "io.circe" %% "circe-generic" % "0.14.1",
  "io.circe" %% "circe-parser" % "0.14.1",
  "com.softwaremill.sttp.client3" %% "core" % "3.8.15", // HTTP client
  "com.softwaremill.sttp.client3" %% "akka-http-backend" % "3.8.15"
)




lazy val root = (project in file("."))
  .aggregate(common, ingestion, processing, anoma@@lyDetection, storage)
  .settings(
    name := "weather-pipeline",
    version := "0.1.0"
  )



lazy val ingestion = (project in file("modules/ingestion"))
  .settings(
    name := "ingestion",
    libraryDependencies ++= Seq(
      "org.apache.kafka" % "kafka-clients" % "3.6.1",
      "io.circe" %% "circe-core" % "0.14.6",
      "io.circe" %% "circe-generic" % "0.14.6",
      "io.circe" %% "circe-parser" % "0.14.6"
    )
  ).dependsOn(common)

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.