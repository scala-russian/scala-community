organization := "com.example"

name := "society-events"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.4"

val korolevVersion = "0.8.1"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-simple" % "1.7.+",
  "com.github.fomkin" %% "korolev-server-blaze" % korolevVersion
)
