name := "mocking-akka-http-client"

version := "1.0"

scalaVersion := "2.11.7"

fork in run := true

mainClass in run := Some("PoliceUKDataClient")

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-http-core-experimental_2.11" % "2.0.1",
  "com.typesafe.akka" % "akka-http-spray-json-experimental_2.11" % "2.0.1",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)
    