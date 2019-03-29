package com.github.merlijn.baker


object Main {

  def main(args: Array[String]): Unit = {

    import mhtml._
    import org.scalajs.dom

    val page: Var[String] = Var("recipes")

    // called when url changes, updates states, triggers re-render
    def navigate(href: String): Unit = href.split('#').tail.headOption match {
      case Some(destination) => page := destination
    }

    // user navigation callback (back, forward buttons)
    dom.window.onpopstate = popState => {
      navigate(dom.window.location.href)
    }

    // the main page layout
    val layout = {

      page.map { p =>

        val path = p.split('/')

        val mainPage = path.headOption.getOrElse("catalogue")
        val subPage = path.tail.headOption.getOrElse("")

        <div>

          { Components.topNavigationBar(mainPage) }

          <div class="container-fluid">
            <div class="row-fluid">
              {
                mainPage match {

                  case "catalogue" => pages.Catalogue(path.tail)
                  case "monitor"   => pages.Monitor(subPage)
                  case _           => <div></div>
                }
              }
            </div>
          </div>

        </div>
      }
    }

    val div = dom.document.getElementById("baker-app")

    mount(div, layout)

    navigate(dom.window.location.href)
  }
}
