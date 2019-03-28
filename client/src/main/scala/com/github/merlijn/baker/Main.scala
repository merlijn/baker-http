package com.github.merlijn.baker

import com.github.merlijn.baker.model.Interaction


object Main {

  def main(args: Array[String]): Unit = {

    import mhtml._
    import org.scalajs.dom

    val page: Var[String] = Var("recipes")

    // called when url changes, updates states, triggers re-render
    def navigate(href: String): Unit = href.split('#').tail.headOption match {
      case Some(destination) => page := destination
    }

    // back, forward navigation callback
    dom.window.onpopstate = popState => {

      navigate(dom.window.location.href)
      println(dom.window.location.href)
    }

    val layout = {

      page.map { p =>

        val mainPage = p.split('/').headOption.getOrElse("catalogue")
        val subPage = p.split('/').tail.headOption.getOrElse("")

        <div>

          { Components.topNavigationBar(mainPage) }

          <div class="container-fluid">
            <div class="row-fluid">
              {
                mainPage match {

                  case "catalogue" => Pages.cataloguePage(subPage)
                  case "monitor"   => Pages.monitorPage(subPage)
                  case _           => <div></div>
                }
              }
            </div>
            <hr />
            { Components.footer }
          </div>
        </div>
      }
    }

    val div = dom.document.getElementById("scalaJsContent")

    mount(div, layout)

    navigate(dom.window.location.href)
  }
}
