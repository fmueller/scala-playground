organization := "de.cupofjava"

name := "word-indexer"

version := "1.0-SNAPSHOT"

scalaVersion := "2.9.2"

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.3",
  "org.scalatest" %% "scalatest" % "1.8" % "test",
  "org.mockito" % "mockito-core" % "1.9.0" % "test"
)