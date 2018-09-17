package org.broadinstitute.reaper.server

import java.io.ByteArrayInputStream

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import akka.stream.{ActorMaterializer, Materializer}
import akka.testkit.TestKit
import better.files.File
import com.bettercloud.vault.{Vault, VaultConfig}
import com.dimafeng.testcontainers.{
  Container,
  ForAllTestContainer,
  MultipleContainers,
  PostgreSQLContainer
}
import com.google.auth.oauth2.{GoogleCredentials, ServiceAccountCredentials}
import io.circe.Printer
import io.circe.syntax._
import org.broadinstitute.clio.client.webclient.ClioWebClient
import org.broadinstitute.clio.integrationtest.ClioDockerComposeContainer
import org.broadinstitute.clio.status.model.VersionInfo
import org.broadinstitute.clio.util.auth.ClioCredentials
import org.broadinstitute.clio.util.json.ModelAutoDerivation
import org.junit.runner.Description
import org.scalatest.{AsyncFlatSpecLike, Matchers}
import slick.jdbc.JdbcBackend._

import scala.collection.JavaConverters._

class IntegrationContainersTest
    extends TestKit(ActorSystem("reaper-int-test"))
    with AsyncFlatSpecLike
    with ForAllTestContainer
    with ModelAutoDerivation
    with Matchers {
  lazy implicit val m: Materializer = ActorMaterializer()

  lazy implicit val description: Description =
    Description.createSuiteDescription(IntegrationContainersTest.super.getClass)

  private val config: ReaperConfig = ReaperConfig
    .load()
    .fold(
      failures => throw new RuntimeException(s"Could not load Reaper config: $failures"),
      config => config
    )

  private val integrationConfig: IntegrationTestConfig = IntegrationTestConfig
    .load()
    .fold(
      failures =>
        throw new RuntimeException(s"Could not load integration test config: $failures"),
      config => config
    )

  /** URL of vault server to use when getting bearer tokens for service accounts. */
  private val vaultUrl = "https://clotho.broadinstitute.org:8200/"

  /** Path in vault to the service account JSON to use in testing. */
  private val vaultPath = "secret/dsde/gotc/test/clio/clio-account.json"

  /** List of possible token-file locations, in order of preference. */
  private val vaultTokenPaths = Seq(
    File("/etc/vault-token-dsde"),
    File(System.getProperty("user.home"), ".vault-token")
  )

  /** Pull service account credentials to use when accessing cloud resources from Vault. */
  protected def loadCredentials(): GoogleCredentials = {
    val vaultToken: String = vaultTokenPaths
      .find(_.exists)
      .map(_.contentAsString.stripLineEnd)
      .getOrElse(sys.error("Vault token not found on filesystem!"))

    val vaultConfig = new VaultConfig()
      .address(vaultUrl)
      .token(vaultToken)
      .build()

    val vaultDriver = new Vault(vaultConfig)
    val accountJSON =
      vaultDriver
        .logical()
        .read(vaultPath)
        .getData
        .asScala
        .toMap[String, String]
        .asJson

    val jsonStream = new ByteArrayInputStream(
      accountJSON.pretty(Printer.noSpaces.copy(dropNullValues = true)).getBytes()
    )
    ServiceAccountCredentials.fromStream(jsonStream)
  }

  private val reaperContainer = ReaperContainer.container
  private val cromwellContainer =
    CromwellContainer.container(integrationConfig.cromwellVersion)
  private val clioContainer: ClioDockerComposeContainer =
    ClioDockerComposeContainer.waitForReadyLog(File("/tmp"))
  private val postgresContainer = PostgreSQLContainer()

  private lazy val db = Database.forURL(
    s"jdbc:postgresql://${postgresContainer.containerIpAddress}:${postgresContainer.mappedPort(5432)}/test",
    driver = "org.postgresql.Driver",
    user = "test",
    password = "test"
  )

  private lazy val clioWebClient: ClioWebClient =
    ClioWebClient(
      clioCredentials,
      clioContainer.clioHost,
      clioContainer.clioPort,
      useHttps = false
    )

  private lazy val clioCredentials = new ClioCredentials(loadCredentials())

  "IntegrationContainersTest" should "be able to connect and query postgres db" in {
    assert(
      db.createSession().createStatement().execute(postgresContainer.testQueryString)
    )
  }

  "IntegrationContainersTest" should "be able to get the current running clio version" in {
    // Will need some way to get this from build
    clioWebClient.getClioServerVersion.runWith(Sink.head).map { json =>
      json.as[VersionInfo] should be(
        Right(VersionInfo("a5bf62570049176f3cc4c72e296a6d1964fa3562"))
      )
    }
  }

  "IntegrationContainersTest" should "be able to get the current running reaper version" in {
    val response = requests.get(
      s"http://${reaperContainer.containerIpAddress}:${reaperContainer.mappedPort(8080)}/version"
    )
    assert(response.statusCode == 200)
    assert(response.text == config.version)
  }

  "IntegrationContainersTest" should "be able to get the current running cromwell version" in {
    val response = requests.get(
      s"http://${cromwellContainer.containerIpAddress}:${cromwellContainer.mappedPort(8000)}/engine/v1/version"
    )
    assert(response.statusCode == 200)
    assert(
      response.text == s"""{\"cromwell\":\"${integrationConfig.cromwellVersion}\"}"""
    )
  }

  override val container: Container = MultipleContainers(
    postgresContainer,
    cromwellContainer,
    clioContainer,
    reaperContainer
  )
}
