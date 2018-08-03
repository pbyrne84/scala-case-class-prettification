
name := "scala-case-class-prettification"

version := "0.1"

scalaVersion := "2.12.6"

scalafmtVersion in ThisBuild := "1.3.0"

scalafmtTestOnCompile in ThisBuild := true

//Set ttl of snapshots so they always refresh
coursierTtl := None

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.12.6"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test

