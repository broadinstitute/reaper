package org.broadinstitute.reaper.server

class ReaperRoutes(config: ReaperConfig) extends cask.MainRoutes {

  override def host: String = "0.0.0.0"

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
    config.version
  }

  initialize()
}

/**
  * Main entry point for the reaper.
  */
object Reaper extends App {

  override def main(args: Array[String]): Unit = {

    val config: ReaperConfig = ReaperConfig
      .load()
      .fold(
        failures =>
          throw new RuntimeException(s"Could not load Reaper config: $failures"),
        config => config
      )

    val mainRoutes = new ReaperRoutes(config)

    mainRoutes.main(args)
  }
}
