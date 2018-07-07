package org.broadinstitute.reaper.server

/**
  * Main entry point for the reaper.
  */
object Reaper {

  def main(args: Array[String]): Unit = {

    val configDescription = ReaperConfig
      .load()
      .fold(
        failures => s"Failed to load config: $failures",
        config => s"Loaded config: $config"
      )

    println(
      s"""               ...
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
         |$configDescription
         |""".stripMargin
    )
  }
}
