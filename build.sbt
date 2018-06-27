import org.broadinstitute.reaper.sbt.Dependencies

enablePlugins(GitVersioning)

/** Project aggregator, doesn't have any sources of its own. */
lazy val reaper = project
  .in(file("."))
  .aggregate(`reaper-server`, `reaper-integration-test`)
  .enablePlugins(ReaperBasePlugin)

/** Main code. */
lazy val `reaper-server` = project
  .enablePlugins(ReaperDockerPlugin)
  .settings(libraryDependencies ++= Dependencies.Server.MainDependencies)

/**
  * Integration-test code.
  *
  * Should run main code through Docker instead of depending on it directly.
  */
lazy val `reaper-integration-test` = project
  .enablePlugins(ReaperBasePlugin)
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(ScalafmtPlugin.scalafmtConfigSettings))
