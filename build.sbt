
name := "kafka-spark-poc"

version := "0.1"

scalaVersion := "2.11.12"

val sparkVersion = "2.4.5"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-yarn" % sparkVersion
)

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion,
  "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion
)

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

assemblyMergeStrategy in assembly := {
  case "reference.conf" => MergeStrategy.concat
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList("META-INF", "services", xs@_*) => MergeStrategy.concat
  case PathList("META-INF", xs) if xs.endsWith(".SF") || xs.endsWith(".DSA") || xs.endsWith(".RSA") => MergeStrategy.discard
  case PathList("META-INF", xs@_*) => MergeStrategy.last
  case _ => MergeStrategy.last
}

test in assembly := {}

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
assemblyJarName in assembly := s"${name.value}_${scalaBinaryVersion.value}-${version.value}.jar"

scalacOptions ++= Seq(
  "-encoding", "utf8"
)
