package org.broadinstitute.reaper.server

import org.scalatest.{EitherValues, FlatSpec, Matchers}

class ReaperConfigSpec extends FlatSpec with Matchers with EitherValues {
  behavior of "ReaperConfig"

  it should "successfully load the reference configuration" in {
    // Version varies by git commit, so just make sure we can read it.
    ReaperConfig.load().right.value.version should not be empty
  }
}
