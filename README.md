# Reaper

Post-processor for Cromwell workflow outputs

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

Internal design proposal [here](https://docs.google.com/document/d/1TeLjjCwxEQGyJ41fudgb164W7LzPX-y9lcQ7GBtW0Zg/edit?usp=sharing).
