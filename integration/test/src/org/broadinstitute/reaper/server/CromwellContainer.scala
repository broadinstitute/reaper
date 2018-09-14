package org.broadinstitute.reaper.server

import com.dimafeng.testcontainers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.ImageFromDockerfile

object CromwellContainer {

  val container = new GenericContainer(
    new ImageFromDockerfile("cromwell_1")
      .withDockerfileFromBuilder(builder => {
        builder
          .from("openjdk:8-slim")
          .env("CROMWELL_VERSION", "34")
          .workDir("/cromwell")
          .run("rm /bin/sh && ln -s /bin/bash /bin/sh")
          .run("apt-get update && apt-get -y install wget")
          .run(
            "wget https://github.com/broadinstitute/cromwell/releases/download/$CROMWELL_VERSION/cromwell-$CROMWELL_VERSION.jar"
          )
          .entryPoint("java -jar cromwell-$CROMWELL_VERSION.jar server")
      }),
    exposedPorts = Seq(8000),
    waitStrategy = Some(Wait.forHttp("/"))
  )
}
