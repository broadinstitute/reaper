# Reaper

Post-processor for Cromwell workflow outputs.

## Overview

Reaper will be a long-running web service responsible for "post-processing" the outputs of production workflows in the cloud.
It will monitor Cromwell for successful workflows, and move the outputs of each workflow it finds into long-term storage 
while recording the files in Clio. It will also parse metrics outputs and persist them to the cloud DB.

## Motivation

The idea for Reaper was born out of pain points we experienced when running post-processing-style tasks in WDL for Arrays
analysis and delivery in the cloud:

* Implicit dependencies on external systems make WDLs more complicated, more difficult to test reproducibly,
  and more dangerous to call-cache
* Injecting access credentials for Broad-internal systems into our cloud pipelines makes those pipelines harder to
  test, deploy, and share
* Transient network errors between systems cause spurious failures in Cromwell and burn operations time on
  diagnosis / resubmission

By taking ownership of tasks which require interacting with external systems, Reaper aims to enable writing cloud pipelines
which are simple to write, test, and operate.

## Getting Started

### Build

`sbt compile`

### Quickly run a server

`sbt reaper-server/run`

### Build server docker image

`sbt reaper-server/docker:publishLocal`

### Generate "staged" contents of docker image

`sbt reaper-server/docker:stage`

### Run the server docker image

`docker run --rm -it broadinstitute/reaper-server:<version>`

### Test that the code is consistently formatted

`sbt scalafmtSbtCheck scalafmtCheck test:scalafmtCheck it:scalafmtCheck`

### Format the code with scalafmt

`sbt scalafmtSbt scalafmt test:scalafmt it:scalafmt`

## More information

Internal documentation is hosted in Confluence [here](https://broadinstitute.atlassian.net/wiki/spaces/DSDEGP/pages/604504131/Reaper+Cloud+workflow+post-processor).
