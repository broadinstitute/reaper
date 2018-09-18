import coursier.Repository
import coursier.maven.MavenRepository
import mill._
import mill.define.Sources
import mill.scalalib._
import mill.scalalib.scalafmt._

import scala.sys.process._

private val ClioRepo = "https://github.com:/broadinstitute/clio"
private val ClioBranch = sys.env.getOrElse("CLIO_BRANCH", "develop")
private val ClioTestkitVersion = "a5bf62570049176f3cc4c72e296a6d1964fa3562"

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
private val LogbackClassicVersion = "1.2.3"

trait GitModule extends Module {
  def gitHashForBranch(repo: String, branch: String) = T.command {
    (s"git ls-remote $repo $branch" #| "cut -f 1").!!.trim
  }
}

trait CommonScalaModule extends ScalaModule {
  def scalaVersion = "2.12.6"

  override def repositories: Seq[Repository] = super.repositories ++ Seq(
    MavenRepository("https://broadinstitute.jfrog.io/broadinstitute/libs-release")
  )
}

trait CommonScalaTest extends TestModule with ScalaModule {
  override def ivyDeps = Agg(
    ivy"org.scalatest::scalatest::$ScalatestVersion".excludeOrg("org.junit")
  )

  def testFrameworks = Seq("org.scalatest.tools.Framework")
}

object server extends CommonScalaModule with ScalafmtModule with GitModule {
  def clioHash = T {gitHashForBranch(ClioRepo, ClioBranch)}

  override def ivyDeps = Agg(
    ivy"ch.qos.logback:logback-classic:$LogbackClassicVersion",
    ivy"com.github.pureconfig::pureconfig::$PureConfigVersion",
    ivy"com.lihaoyi::cask:$CaskVersion",
    ivy"com.typesafe.akka::akka-stream::$AkkaVersion",
    ivy"com.typesafe.akka::akka-http::$AkkaHttpVersion",
    ivy"org.broadinstitute::clio-client::${clioHash()}",
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

  object test extends Tests with CommonScalaTest with ScalafmtModule
}

object integration extends CommonScalaModule with ScalafmtModule {
  override def moduleDeps = Seq(server)
  // Include server resources for reference reaper configuration
  override def resources: Sources = T.sources{super.resources() ++ server.resources()}

  object test extends Tests with ScalafmtModule with CommonScalaTest {
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
