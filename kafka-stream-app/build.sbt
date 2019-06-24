name := "kafka-stream-app"

version := "0.1"

scalaVersion := "2.13.0"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "0.11.0.0",
  "org.apache.avro"  %  "avro"  %  "1.7.7",
  "com.typesafe.play" % "play-json_2.11" % "2.4.6",
  "com.typesafe.akka" %% "akka-actor" % "2.5.23",
  "com.typesafe.akka" %% "akka-stream" % "2.5.23",
  "org.apache.kafka" % "kafka-clients" % "0.8.2.2",
)