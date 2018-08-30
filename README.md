# Reaper

Post-processor for Cromwell workflow outputs

## Getting Started

### Install Mill

http://www.lihaoyi.com/mill/#installation

### Build

`mill server.compile`

### Generate Intellij .idea files

`mill mill.scalalib.GenIdea/idea`

### Quickly run a server

`mill server`

### Run the server while watching for source changes

`mill -w server`

### Run the tests

`mill server.test`

### Format the code with scalafmt

`mill server.reformat`

## Format all the code with scalafmt

`mill mill.scalalib.scalafmt.ScalafmtModule/reformatAll __.sources`

## More information

Internal design proposal [here](https://docs.google.com/document/d/1TeLjjCwxEQGyJ41fudgb164W7LzPX-y9lcQ7GBtW0Zg/edit?usp=sharing).
