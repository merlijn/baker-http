package com.github.merlijn.baker

import com.github.merlijn.baker.model.Interaction
import com.github.merlijn.baker.model.JsonCodecs._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.util.{Failure, Success}
import Components._

import scala.xml.Elem

object Main {

  def main(args: Array[String]): Unit = {

    import mhtml._
    import org.scalajs.dom

    val noContent: Rx[Elem] = Var(<div></div>)
    val page: Var[String] = Var("recipes")

    val interactions: Var[Seq[Interaction]] = Var(Seq.empty)

    def navigate(href: String): Unit = href.split('#').tail.headOption match {
      case Some(destination @ "catalogue/interactions") =>

        dom.ext.Ajax.get("/catalogue/interactions").onComplete {
          case Success(xhr) =>

            val json = xhr.responseText
            interactions := decodeUnsafe[Seq[Interaction]](json)
            page := destination

          case Failure(e) =>
            System.err.println(e.toString)
        }
      case Some(destination) =>
        page := destination
    }

    // back, forward navigation callback
    dom.window.onpopstate = popState => {

      navigate(dom.window.location.href)
      println(dom.window.location.href)
    }

    def cataloguePage(subPage: String) = {
      <div>
        <div class="span3">
          { Menu("catalogue", Seq("Recipes", "Interactions", "Ingredients"), subPage) }
        </div>
        <div class="span9">
          <div class="row-fluid">
            {
              if (subPage == "interactions")
                interactions.map(interactionsTable)
              else
                noContent
            }
          </div>
        </div>
      </div>
    }

    val layout = {

      page.map { p =>

        val mainPage = p.split('/').headOption.getOrElse("catalogue")
        val subPage = p.split('/').tail.headOption.getOrElse("")

        <div>

          { topNavigationBar(mainPage) }

          <div class="container-fluid">
            <div class="row-fluid">
              {
                if (mainPage == "catalogue")
                  cataloguePage(subPage)
                else
                  <div></div>
              }
            </div>
            <hr />
            <footer>
              <a href = "https://www.github.com/merlijn/baker-http">Github code</a>
            </footer>
          </div>
        </div>
      }
    }


    val div = dom.document.getElementById("scalaJsContent")

    mount(div, layout)

    navigate(dom.window.location.href)
  }
}
