import coursier.Repository
import coursier.maven.MavenRepository
import mill._
import mill.scalalib._
import mill.scalalib.scalafmt._

/** Version of Clio to build / test against. */
private val ClioVersion = "9dd03079781f6855a8e4582550fbdacd1433f4d2"
private val ClioTestkitVersion = "d695feea7a50c073bf3764fd329f20e9e49d515d"

private val AkkaHttpVersion = "10.1.3"
private val AkkaVersion = "2.5.14"
private val BetterFilesVersion = "3.5.0"
private val CaskVersion = "0.1.9"
private val PostgresVersion = "42.2.5"
private val PureConfigVersion = "0.9.1"
private val TestContainersVersion = "0.20.0"
private val ScalatestVersion = "3.0.5"
private val SlickVersion = "3.2.3"
private val VaultJavaDriverVersion = "3.1.0"
private val CirceVersion = "0.9.3"
private val RequestsVersion = "0.1.4"
private val uJsonVersion = "0.6.6"
private val PostgresTestContainerVersion = "1.8.3"

trait ScalaVersion extends ScalaModule {
  def scalaVersion = "2.12.6"

  override def repositories: Seq[Repository] = super.repositories ++ Seq(
    MavenRepository("https://broadinstitute.jfrog.io/broadinstitute/libs-release")
  )
}

trait ScalaTest extends TestModule with ScalaModule {
  override def ivyDeps = Agg(
    ivy"org.scalatest::scalatest::$ScalatestVersion".excludeOrg("org.junit")
  )

  def testFrameworks = Seq("org.scalatest.tools.Framework")
}

object server extends ScalaVersion with ScalafmtModule {
  override def ivyDeps = Agg(
    ivy"com.github.pureconfig::pureconfig::$PureConfigVersion",
    ivy"com.lihaoyi::cask:$CaskVersion",
    ivy"com.typesafe.akka::akka-stream::$AkkaVersion",
    ivy"com.typesafe.akka::akka-http::$AkkaHttpVersion",
    ivy"org.broadinstitute::clio-client::$ClioVersion",
    ivy"com.typesafe.slick::slick::$SlickVersion",
    ivy"com.typesafe.slick::slick-hikaricp::$SlickVersion",
    ivy"org.postgresql:postgresql:$PostgresVersion",
    ivy"com.bettercloud:vault-java-driver:$VaultJavaDriverVersion",
    ivy"com.github.pathikrit::better-files::$BetterFilesVersion",
    ivy"io.circe::circe-core::$CirceVersion",
    ivy"io.circe::circe-generic::$CirceVersion",
    ivy"io.circe::circe-generic-extras::$CirceVersion",
    ivy"io.circe::circe-parser::$CirceVersion"
  )

  object test extends Tests with ScalaTest with ScalafmtModule
}

object integration extends ScalaVersion with ScalafmtModule {
  override def moduleDeps = Seq(server)

  object test extends Tests with ScalafmtModule with ScalaTest {
    override def ivyDeps = super.ivyDeps() ++ Agg(
      ivy"com.dimafeng::testcontainers-scala::$TestContainersVersion",
      ivy"org.testcontainers:postgresql:$PostgresTestContainerVersion",
      ivy"org.broadinstitute::clio-integration-testkit::$ClioTestkitVersion",
      ivy"com.typesafe.akka::akka-http-testkit:$AkkaHttpVersion",
      ivy"com.typesafe.akka::akka-stream-testkit::$AkkaVersion",
      ivy"com.lihaoyi::requests:$RequestsVersion",
      ivy"com.lihaoyi::ujson:$uJsonVersion")
  }
}