package org.broadinstitute.reaper.server

import pureconfig.error.ConfigReaderFailures

/**
  * Model for external configuration which should be loaded in from
  * disk / system properties.
  *
  * Reaper components which need to take configuration parameters should
  * create their own model classes, and add an instance of that model as
  * a parameter to this class.
  */
case class ReaperConfig(version: String)

object ReaperConfig {

  /**
    * Config namespace which is expected to encapsulate all parameters
    * for the Reaper server.
    */
  val ConfigRoot: String = "reaper.server"

  /**
    * Load application config from disk / system properties, and attempt
    * to parse the result into Reaper's config model.
    */
  def load(): Either[ConfigReaderFailures, ReaperConfig] =
    pureconfig.loadConfig[ReaperConfig](ConfigRoot)
}
