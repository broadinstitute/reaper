package org.broadinstitute.reaper.plugins

import com.typesafe.sbt.GitBranchPrompt
import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin
import org.broadinstitute.reaper.sbt.Dependencies

/** Base sbt plugin containing settings which should be applied to all sub-projects by default. */
object ReaperBasePlugin extends AutoPlugin {
  import com.typesafe.sbt.SbtGit._
  import org.scalafmt.sbt.ScalafmtPlugin.autoImport._
  import scoverage.ScoverageKeys._

  // Don't load these settings until after the base plugins.
  override def requires: Plugins = JvmPlugin && GitBranchPrompt

  /**
    * Various compiler tweaks. More info available via:
    * - http://tpolecat.github.io/2017/04/25/scalac-flags.html
    * - https://blog.threatstack.com/useful-scalac-options-for-better-scala-development-part-1
    * - sbt 'set scalacOptions in Compile += "-help"' compile
    * - sbt 'set scalacOptions in Compile += "-X"' compile
    * - sbt 'set scalacOptions in Compile += "-Y"' compile
    */
  private val compilerSettings: Seq[String] = Seq(
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
    "-Ywarn-unused",
    "-Ywarn-value-discard"
  )

  /** sbt console warnings should not be fatal. */
  private val consoleSettings: Seq[String] = compilerSettings filterNot Set(
    "-Xfatal-warnings",
    "-Xlint",
    "-Ywarn-unused"
  )

  /**
    * Don't generate warnings for missing links.
    *
    * Since warnings are now errors, using this override until someone discovers a way to fix links.
    * http://stackoverflow.com/questions/31488335/scaladoc-2-11-6-fails-on-throws-tag-with-unable-to-find-any-member-to-link#31497874
    */
  private val docSettings: Seq[String] = Seq(
    "-no-link-warnings"
  )

  /** Generate a version using the full git hash, or "UNKNOWN". */
  private lazy val gitShaVersion =
    Def.setting(git.gitHeadCommit.value.getOrElse("UNKNOWN"))

  /** Write the version information into a configuration file. */
  private lazy val writeVersionConfig = Def.task {
    val projectName = name.value
    val projectConfig = projectName.replace("reaper-", "reaper.")
    val file = (resourceManaged in Compile).value / s"$projectName-version.conf"
    val contents = Seq(s"$projectConfig.version: ${version.value}")
    IO.writeLines(file, contents)
    Seq(file)
  }

  val BroadArtifactoryHost: String = "https://broadinstitute.jfrog.io/broadinstitute"

  private val broadArtifactory: Resolver =
    "Broad Artifactory Releases" at s"$BroadArtifactoryHost/libs-release/"

  // Settings to apply at the build scope.
  override def buildSettings: Seq[Def.Setting[_]] = Seq(
    organization := "org.broadinstitute",
    scalaVersion := Dependencies.ScalaVersion,
    git.formattedShaVersion := Some(gitShaVersion.value),
    coverageHighlighting := false,
    scalafmtConfig := Some((baseDirectory in ThisBuild)(_ / ".scalafmt.conf").value),
    scalafmtOnCompile := true,
    scalacOptions ++= compilerSettings
  )

  // Settings to apply at per-project scopes.
  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    scalacOptions in (Compile, doc) ++= docSettings,
    scalacOptions in (Compile, console) := consoleSettings,
    resourceGenerators in Compile += writeVersionConfig.taskValue,
    fork in run := true,
    resolvers += broadArtifactory
  )
}
