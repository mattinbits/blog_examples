name := "akka-mutable-seq"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.7"

libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.4.7"

mainClass := Some("com.mjlivesey.mutableakka.Example")
