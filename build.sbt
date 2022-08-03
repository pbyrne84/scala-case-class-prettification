lazy val baseName = "scala-case-class-prettification"

version := "0.1"

val allScalaVersion = "3.1.3"
scalaVersion := allScalaVersion

lazy val base = (project in file("modules/" + baseName)).settings(
  name := baseName,
  scalaVersion := allScalaVersion,
  libraryDependencies ++= Vector(
    scalaTest % Test
  )
)

lazy val diff = (project in file("modules/" + baseName + "-diff"))
  .dependsOn(base)
  .settings(
    name := baseName + "-diff",
    scalaVersion := allScalaVersion
  )

lazy val test = (project in file("modules/" + baseName + "-test"))
  .dependsOn(base)
  .settings(
    name := baseName + "-test",
    scalaVersion := allScalaVersion,
    libraryDependencies ++= Vector(
      scalaTest % "provided"
    )
  )

lazy val all = (project in file("."))
  .aggregate(base, diff, test)
  .settings(
    scalaVersion := allScalaVersion,
    skip in publish := true
  )

val scalaTest = "org.scalatest" %% "scalatest" % "3.2.13"
