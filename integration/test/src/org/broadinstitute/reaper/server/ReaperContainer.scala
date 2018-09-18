package org.broadinstitute.reaper.server

import java.nio.file.Paths

import com.dimafeng.testcontainers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.ImageFromDockerfile

object ReaperContainer {

  val container = new GenericContainer(
    new ImageFromDockerfile("reaper_1")
      .withFileFromPath("reaper", Paths.get("."))
      .withDockerfileFromBuilder(builder => {
        builder
          .from("openjdk:8-slim")
          .env("SCALA_VERSION", "2.12.6")
          .env("MILL_VERSION", "0.2.7")
          .workDir("/reaper")
          .run("ln -sf /bin/bash /bin/sh")
          .run("apt-get update && apt-get -y install curl git")
          .run("touch /usr/lib/jvm/java-8-openjdk-amd64/release")
          .run(
            s"""curl -fsL https://downloads.typesafe.com/scala/$$SCALA_VERSION/scala-$$SCALA_VERSION.tgz | tar xfz - -C /root/ && \\
               |echo >> /root/.bashrc && \\
               |echo "export PATH=~/scala-$$SCALA_VERSION/bin:$$PATH" >> /root/.bashrc""".stripMargin
          )
          .run(s"""curl -L -o /usr/local/bin/mill https://github.com/lihaoyi/mill/releases/download/$$MILL_VERSION/$$MILL_VERSION && \\
               |chmod +x /usr/local/bin/mill && \\
               |touch build.sc && \\
               |mill -i resolve _ && \\
               |rm build.sc""".stripMargin)
          .expose(8080)
          .add("reaper", "/reaper")
          .run("mill server.compile")
          .entryPoint("mill server.run")
      }),
    exposedPorts = Seq(8080),
    waitStrategy = Some(Wait.forHttp("/"))
  )
}
