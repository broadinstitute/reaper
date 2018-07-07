package org.broadinstitute.reaper.sbt

import sbt._

object Dependencies {

  /** Version of Scala to build reaper with. */
  val ScalaVersion = "2.12.6"

  /** Version of Clio to build / test against. */
  val ClioVersion = "9dd03079781f6855a8e4582550fbdacd1433f4d2"

  /** Version of Cromwell to build / test against. */
  val CromwellVersion = "32"

  // External libraries.
  private val AkkaVersion = "2.5.13"
  private val AkkaHttpVersion = "10.1.3"
  private val BetterMonadicForVersion = "0.2.4"
  private val PureConfigVersion = "0.9.1"
  private val ScalatestVersion = "3.0.5"

  object Server {

    private val main = Seq(
      "com.github.pureconfig" %% "pureconfig" % PureConfigVersion,
      compilerPlugin("com.olegpy" %% "better-monadic-for" % BetterMonadicForVersion),
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "org.broadinstitute" %% "clio-client" % ClioVersion,
      "org.broadinstitute" %% "cromwell-api-client" % CromwellVersion
    )

    private val test = Seq(
      "org.scalatest" %% "scalatest" % ScalatestVersion
    ).map(_ % Test)

    val Dependencies: Seq[ModuleID] = main ++ test
  }
}
