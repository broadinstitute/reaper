import coursier.Repository
import coursier.maven.MavenRepository
import mill._
import scalalib._
import scalafmt._

trait ScalaTest extends TestModule {
  private val ScalatestVersion = "3.0.5"
  override def ivyDeps = Agg(ivy"org.scalatest::scalatest::$ScalatestVersion".excludeOrg("org.junit"))
  def testFrameworks = Seq("org.scalatest.tools.Framework")
}

object server extends ScalaModule with ScalafmtModule {
  def scalaVersion = "2.12.6"
  /** Version of Clio to build / test against. */
  val ClioVersion = "9dd03079781f6855a8e4582550fbdacd1433f4d2"

  private val AkkaVersion = "2.5.13"
  private val AkkaHttpVersion = "10.1.3"
  private val CaskVersion = "0.1.9"
  private val PureConfigVersion = "0.9.1"

  override def repositories: Seq[Repository] = super.repositories ++ Seq(
    MavenRepository("https://broadinstitute.jfrog.io/broadinstitute/libs-release")
  )

  override def ivyDeps = Agg(
    ivy"com.github.pureconfig::pureconfig::$PureConfigVersion",
    ivy"com.lihaoyi::cask:$CaskVersion",
    ivy"com.typesafe.akka::akka-stream::$AkkaVersion",
    ivy"com.typesafe.akka::akka-http::$AkkaHttpVersion",
    ivy"org.broadinstitute::clio-client::$ClioVersion"
  )

  object test extends Tests with ScalaTest
}

