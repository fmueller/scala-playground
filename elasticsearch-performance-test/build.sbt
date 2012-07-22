organization := "de.cupofjava"

name := "elasticsearch-performance-test"

version := "1.0-SNAPSHOT"

scalaVersion := "2.9.2"

libraryDependencies ++= Seq(
  "default" %% "scalastic" % "0.0.6-SNAPSHOT",
  "commons-lang" % "commons-lang" % "2.4",
  "commons-io" % "commons-io" % "2.3",
  "org.scalatest" %% "scalatest" % "1.8" % "test"
)

parallelExecution in Test := false

resolvers += "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases"