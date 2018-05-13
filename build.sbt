import sbt.Keys.organization

lazy val logger = (project in file("logger"))
  .settings(
    Seq(
      name := "cluster-event-logger",
      organization := "com.harko",
      version := "0.1",
      scalaVersion := "2.12.6"
    ),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.5.12",
      "com.typesafe.akka" %% "akka-http" % "10.1.1",
      "com.typesafe.akka" %% "akka-stream" % "2.5.12",
      "com.typesafe.akka" %% "akka-persistence" % "2.5.12",
      "com.typesafe.akka" %% "akka-cluster" % "2.5.12",
      "com.lightbend.akka.management" %% "akka-management" % "0.12.0",
      "com.typesafe.akka" %% "akka-persistence-query" % "2.5.12",
      "org.json4s" %% "json4s-jackson" % "3.5.3",
      "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.84"
    )
  )


lazy val visualizerjs = (project in file("visualizer.js"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    Seq(
      name := "visualizer-js",
      organization := "com.harko",
      version := "0.1",
      scalaVersion := "2.12.6"
    ),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.5",
      "org.akka-js" %%% "akkajsactor" % "1.2.5.12",
      "io.circe" %%% "circe-core" % "0.9.3",
      "io.circe" %%% "circe-generic" % "0.9.3",
      "io.circe" %%% "circe-parser" % "0.9.3"
    )
  )