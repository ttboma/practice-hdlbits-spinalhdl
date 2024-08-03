ThisBuild / version := "1.0"
ThisBuild / scalaVersion := "2.12.19" 
ThisBuild / organization := "org.example"

val spinalVersion = "1.10.1"
val spinalCore = "com.github.spinalhdl" %% "spinalhdl-core" % spinalVersion
val spinalLib = "com.github.spinalhdl" %% "spinalhdl-lib" % spinalVersion
val spinalIdslPlugin = compilerPlugin("com.github.spinalhdl" %% "spinalhdl-idsl-plugin" % spinalVersion)
val scalaTest = "org.scalatest" %% "scalatest" % "3.2.18" % "test"
val bloop = "ch.epfl.scala" % "sbt-bloop_2.12_1.0" % "1.5.17"

lazy val root = (project in file("."))
  .settings(
    name := "spinal-hdl-demo",
    libraryDependencies ++= Seq(bloop, spinalCore, spinalLib, spinalIdslPlugin, scalaTest)
  )

fork := true