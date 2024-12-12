lazy val baseName = "scala-case-class-prettification"

version := "0.1"

val scala3Version = "3.4.2"
val scala213Version = "2.13.10"
// val scala212Version = "2.12.17"
lazy val supportedScalaVersions = List(scala3Version, scala213Version)

scalaVersion := scala3Version

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
publishMavenStyle := true

inThisBuild(
  List(
    organization := "uk.org.devthings",
    homepage := Some(url("https://github.com/pbyrne84/scala-case-class-prettification")),
    // Alternatively License.Apache2 see https://github.com/sbt/librarymanagement/blob/develop/core/src/main/scala/sbt/librarymanagement/License.scala
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "pbyrne84",
        "Patrick Byrne",
        "pbyrne84@gmail.com",
        url("https://devthings.org.uk/")
      )
    )
  )
)

//not to be used in ci, intellij has got a bit bumpy in the format on save on optimize imports across the project
val formatAndTest =
  taskKey[Unit](
    "format all code then run tests, do not use on CI as any changes will not be committed"
  )

lazy val commonSettings = Seq(
  scalaVersion := scala213Version,
  crossScalaVersions := supportedScalaVersions,
  scalacOptions ++= Seq(
    "-encoding",
    "utf8",
    "-feature",
    "-language:implicitConversions",
    "-language:existentials",
    "-unchecked"
  ) ++
    (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 13)) => Seq("-Ytasty-reader") // flags only needed in Scala 2
      case Some((3, _)) => Seq("-no-indent") // flags only needed in Scala 3
      case _ => Seq.empty
    }),
  formatAndTest := {
    (Test / test)
      .dependsOn(Compile / scalafmtAll)
      .dependsOn(Test / scalafmtAll)
  }.value,
  Test / test := (Test / test)
    .dependsOn(Compile / scalafmtCheck)
    .dependsOn(Test / scalafmtCheck)
    .value
)

lazy val prettifiedBase = (project in file("modules/" + baseName)).settings(
  name := baseName,
  commonSettings,
  libraryDependencies ++= Vector(
    scalaTest % Test,
    "org.scalamock" %% "scalamock" % "6.0.0" % Test
  )
)

lazy val prettifiedDiff = (project in file("modules/" + baseName + "-diff"))
  .dependsOn(prettifiedBase)
  .settings(
    name := baseName + "-diff",
    commonSettings
  )

lazy val prettifiedTest = (project in file("modules/" + baseName + "-test"))
  .dependsOn(prettifiedBase)
  .settings(
    name := baseName + "-test",
    commonSettings,
    libraryDependencies ++= Vector(
      scalaTest % "provided"
    )
  )

lazy val caseClassPrettificationAll = (project in file("."))
  .aggregate(prettifiedBase, prettifiedDiff, prettifiedTest)
  .settings(
    commonSettings,
    publish / skip := true
  )

val scalaTest = "org.scalatest" %% "scalatest" % "3.2.13"
