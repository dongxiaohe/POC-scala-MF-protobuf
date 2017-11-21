
name := "POC-maxwell-smarts-proto-buf"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies += "com.trueaccord.scalapb" %% "compilerplugin" % "0.6.6"
libraryDependencies += "com.google.protobuf" % "protobuf-java-util" % "3.4.0"
libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.17"

PB.targets in Compile := Seq(
  PB.gens.java -> (sourceManaged in Compile).value
)

mainClass in(Compile, run) := Some("Main")