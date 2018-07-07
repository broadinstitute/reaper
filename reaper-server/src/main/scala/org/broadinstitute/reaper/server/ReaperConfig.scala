package org.broadinstitute.reaper.server

import pureconfig.error.ConfigReaderFailures

case class ReaperConfig(version: String)

object ReaperConfig {

  val ConfigRoot: String = "reaper.server"

  def load(): Either[ConfigReaderFailures, ReaperConfig] =
    pureconfig.loadConfig[ReaperConfig](ConfigRoot)
}
