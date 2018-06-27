package org.broadinstitute.reaper.plugins

import com.typesafe.sbt.packager.archetypes.scripts.AshScriptPlugin
import com.typesafe.sbt.packager.docker.{DockerKeys, DockerPlugin}
import com.typesafe.sbt.packager.linux.LinuxKeys
import sbt._
import sbt.Keys._

/** sbt plugin to enable bundling an Reaper subproject as an executable Docker image. */
object ReaperDockerPlugin extends AutoPlugin with DockerKeys with LinuxKeys {

  override def requires: Plugins = ReaperBasePlugin && DockerPlugin && AshScriptPlugin

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    dockerBaseImage := "openjdk:8-alpine3.7",
    dockerRepository := Some("broadinstitute"),
    dockerExposedPorts := Seq(8080, 31757),
    dockerLabels := Map("REAPER_VERSION" -> version.value),
    defaultLinuxInstallLocation in DockerPlugin.autoImport.Docker := "/app"
  )
}
