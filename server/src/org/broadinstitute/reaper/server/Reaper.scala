package org.broadinstitute.reaper.server

import pureconfig.error.ConfigReaderFailures

object ReaperRoutes extends cask.Routes {

  val reaperArt: String = s"""               ...
                             |             ;::::;
                             |           ;::::; :;
                             |         ;:::::'   :;
                             |        ;:::::;     ;.
                             |       ,:::::'       ;           OOO\\
                             |       ::::::;       ;          OOOOO\\
                             |       ;:::::;       ;         OOOOOOOO
                             |      ,;::::::;     ;'         / OOOOOOO
                             |    ;:::::::::`. ,,,;.        /  / DOOOOOO
                             |  .';:::::::::::::::::;,     /  /     DOOOO
                             | ,::::::;::::::;;;;::::;,   /  /        DOOO
                             |;`::::::`'::::::;;;::::: ,#/  /          DOOO
                             |:`:::::::`;::::::;;::: ;::#  /            DOOO
                             |::`:::::::`;:::::::: ;::::# /              DOO
                             |`:`:::::::`;:::::: ;::::::#/               DOO
                             | :::`:::::::`;; ;:::::::::##                OO
                             | ::::`:::::::`;::::::::;:::#                OO
                             | `:::::`::::::::::::;'`:;::#                O
                             |  `:::::`::::::::;' /  / `:#
                             |   ::::::`:::::;'  /  /   `#
                             |""".stripMargin

  @cask.get("/")
  def reaper(): String = {
    reaperArt
  }

  @cask.get("/health")
  def health(): String = {
    "green"
  }

  @cask.get("/version")
  def version(): String = {
    Reaper.config.version
  }

  initialize()
}

/**
  * Main entry point for the reaper.
  */
object Reaper extends cask.Main(ReaperRoutes) {

  val config: ReaperConfig = ReaperConfig
    .load()
    .fold(
      failures => throw new RuntimeException(s"Could not load Reaper config: $failures"),
      config => config
    )

  override def main(args: Array[String]): Unit = {

    val configDescription: String = ReaperConfig
      .load()
      .fold(
        failures => s"Failed to load config: $failures",
        config => s"Loaded config: $config"
      )

    super.main(args)
  }
}
