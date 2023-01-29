name := "hio"

version := "0.0.1"

scalaVersion := "2.12.12"

val hadoopVersion = "3.3.1"
libraryDependencies ++= Seq(
  "org.apache.hadoop" % "hadoop-common" % hadoopVersion % Provided,
  "org.apache.hadoop" % "hadoop-aws" % hadoopVersion % Provided,
  "org.apache.hadoop" % "hadoop-azure" % hadoopVersion % Provided,
  "com.microsoft.azure" % "azure-data-lake-store-sdk" % "2.3.10" % Provided,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test,
  "com.lihaoyi" %% "os-lib" % "0.7.1" % Test
)

// test suite settings
Test / fork := true
javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")
// Show runtime of tests
Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD")