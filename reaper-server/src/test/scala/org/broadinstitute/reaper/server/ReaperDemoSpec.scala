package org.broadinstitute.reaper.server

import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}

/**
  * Demo test suite to prove the build job can find & load test reports.
  *
  * TODO: Remove this when we have real functionality to test.
  */
class ReaperDemoSpec extends FlatSpec with Matchers {
  behavior of "Reaper"

  it should "be able to load generated version config" in {
    ConfigFactory.load().hasPath("reaper.server.version") should be(true)
  }
}
