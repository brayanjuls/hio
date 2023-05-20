name := "hio"
organization := "com.brayanjules"

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

// Release Process
import xerial.sbt.Sonatype._
sonatypeProjectHosting := Some(
  GitHubHosting("brayanjuls", "hio", "Brayan Jules", "brayan1213@gmail.com")
)

licenses += ("MIT" -> url("http://opensource.org/licenses/MIT"))
publishMavenStyle  := true

sonatypeProfileName    := "com.brayanjules"
sonatypeCredentialHost := "s01.oss.sonatype.org"
sonatypeRepository     := "https://s01.oss.sonatype.org/service/local"
publishTo              := sonatypePublishToBundle.value

import ReleaseTransformations._
releaseCrossBuild := true
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies, // check that there are no SNAPSHOT dependencies
  inquireVersions,           // ask user to enter the current and next version
  runClean,                  // clean
  runTest,                   // run tests
  setReleaseVersion,         // set release version in version.sbt
  commitReleaseVersion,      // commit the release version
  tagRelease,                // create git tag
  releaseStepCommandAndRemaining(
    "+publishSigned"
  ),                 // run +publishSigned command to sonatype stage release
  setNextVersion,    // set next version in version.sbt
  commitNextVersion, // commit next version
  releaseStepCommand("sonatypeBundleRelease"), // run sonatypeRelease and publish to maven central
  pushChanges                                  // push changes to git
)
