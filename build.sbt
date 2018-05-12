name := "cluster-event-logger"

organization := "com.harko"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.12"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.1"

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.12"

libraryDependencies += "com.typesafe.akka" %% "akka-persistence" % "2.5.12"

libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % "2.5.12"

libraryDependencies += "com.lightbend.akka.management" %% "akka-management" % "0.12.0"

libraryDependencies += "com.typesafe.akka" %% "akka-persistence-query" % "2.5.12"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.5.3"

libraryDependencies += "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.84"
