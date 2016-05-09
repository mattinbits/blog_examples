import sbt._
import Keys._

object BuildSettings {
  val buildSettings = Defaults.coreDefaultSettings ++ Seq(
    version := "1.0.0",
    scalaVersion := "2.11.8",
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += Resolver.sonatypeRepo("releases"),
    addCompilerPlugin("org.scalamacros" % "paradise_2.11.8" % "2.1.0"),
    scalacOptions ++= Seq()
  )
}

object MyBuild extends Build {

  import BuildSettings._

  lazy val root: Project = Project(
    "root",
    file("."),
    settings = buildSettings ++ Seq(
      run <<= run in Compile in core)
  ) aggregate(macros, core)


  lazy val macros: Project = Project(
    "macros",
    file("macros"),
    settings = buildSettings ++ Seq(
      libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _)
    )
  )

  lazy val core: Project = Project(
    "core",
    file("core"),
    settings = buildSettings ++ Seq(
      libraryDependencies += "org.apache.hive" % "hive-exec" % "0.13.0",
      libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6"

    )
  ) dependsOn(macros)
}