
lazy val baseName = "scala-case-class-prettification"

version := "0.1"

scalaVersion := "2.12.6"

scalafmtVersion in ThisBuild := "1.3.0"

scalafmtTestOnCompile in ThisBuild := true

//Set ttl of snapshots so they always refresh
coursierTtl := None
lazy val base = (project in file("modules/" + baseName)).settings(
  name := baseName,
  scalaVersion := "2.12.6",
  libraryDependencies := Vector(
    "org.scala-lang" % "scala-reflect" % "2.12.6",
    scalaTest % Test
  )
)

lazy val diff = (project in file("modules/" + baseName + "-diff"))
  .dependsOn(base)
  .settings(
    name := baseName + "-diff",
    scalaVersion := "2.12.6"
  )

lazy val test = (project in file("modules/" + baseName + "-test"))
  .dependsOn(base)
  .settings(
    name := baseName + "-test",
    scalaVersion := "2.12.6",
    libraryDependencies := Vector(
      scalaTest % "provided"
    )
  )

lazy val all = (project in file("."))
  .aggregate(base, diff, test)
  .settings(
    scalaVersion := "2.12.6",
    skip in publish := true
  )

val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"