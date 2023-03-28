lazy val baseName = "scala-case-class-prettification"

version := "0.1"

val scala3Version = "3.1.3"
val scala213Version = "2.13.8"
val scala212Version = "2.12.17"
lazy val supportedScalaVersions = List(scala3Version, scala213Version, scala212Version)

scalaVersion := scala3Version

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
    scalaTest % Test
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
