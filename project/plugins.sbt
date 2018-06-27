// For more info on these plugins, see https://broadinstitute.atlassian.net/wiki/pages/viewpage.action?pageId=114531509

val SbtCoursierVersion = "1.0.3"
val SbtGitVersion = "1.0.0"
val SbtNativePackagerVersion = "1.3.5"
val SbtScoverageVersion = "1.5.1"
val ScalafmtVersion = "1.6.0-RC3"

val Slf4jVersion = "1.7.25"

addSbtPlugin("com.geirsson" % "sbt-scalafmt" % ScalafmtVersion)
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % SbtGitVersion)
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % SbtNativePackagerVersion)
addSbtPlugin("io.get-coursier" % "sbt-coursier" % SbtCoursierVersion)
addSbtPlugin("org.scoverage" % "sbt-scoverage" % SbtScoverageVersion)

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-nop" % Slf4jVersion
)

// https://github.com/coursier/coursier/issues/450
classpathTypes += "maven-plugin"

// Various compiler tweaks for our build code.
// More info available via:
//   https://tpolecat.github.io/2014/04/11/scalac-flags.html
//   https://blog.threatstack.com/useful-scalac-options-for-better-scala-development-part-1
//   sbt 'set scalacOptions in Compile += "-help"' compile
//   sbt 'set scalacOptions in Compile += "-X"' compile
//   sbt 'set scalacOptions in Compile += "-Y"' compile
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-explaintypes",
  "-feature",
  "-target:jvm-1.8",
  "-unchecked",
  "-Xcheckinit",
  "-Xfatal-warnings",
  "-Xfuture",
  "-Xlint",
  "-Xmax-classfile-name",
  "200",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-extra-implicit",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Ywarn-nullary-override",
  "-Ywarn-nullary-unit",
  "-Ywarn-numeric-widen",
  /*
   * Disable unused-imports warning because sbt dumps a standard preamble of imports into its
   * build files before compiling them, causing a ton of warnings we can't do anything about
   */
  "-Ywarn-unused:-imports",
  "-Ywarn-value-discard"
)

inThisBuild(
  Seq(
    scalafmtOnCompile := true,
    // Use the scalafmt config in the root directory.
    scalafmtConfig := Some(baseDirectory(_.getParentFile / ".scalafmt.conf").value)
  )
)
