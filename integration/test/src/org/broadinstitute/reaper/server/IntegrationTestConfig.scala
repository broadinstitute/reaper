package org.broadinstitute.reaper.server

import pureconfig.error.ConfigReaderFailures

case class IntegrationTestConfig(cromwellVersion: String)

object IntegrationTestConfig {

  /**
    * Config namespace which is expected to encapsulate all parameters
    * for the Reaper server.
    */
  val ConfigRoot: String = "integration"

  /**
    * Load application config from disk / system properties, and attempt
    * to parse the result into Reaper's config model.
    */
  def load(): Either[ConfigReaderFailures, IntegrationTestConfig] =
    pureconfig.loadConfig[IntegrationTestConfig](ConfigRoot)
}
