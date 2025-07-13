lazy val storage = (project in file("."))
  .settings(
    name := "storage",
    libraryDependencies ++= Seq(
      "org.mongodb.scala" %% "mongo-scala-driver" % "4.9.0",
      "org.typelevel" %% "cats-effect" % "3.5.1",
      "com.typesafe.play" %% "play-json" % "2.9.4"

    )
  )
