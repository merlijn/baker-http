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

    def navigate(dest: String): Unit = dest match {
      case "catalogue/interactions" =>

        dom.ext.Ajax.get("/catalogue/interactions").onComplete {
          case Success(xhr) =>

            val json = xhr.responseText
            interactions := decodeUnsafe[Seq[Interaction]](json)
            page := dest

          case Failure(e) =>
            System.err.println(e.toString)
        }
      case other =>
        page := dest
    }

    // back, forward navigation callback
    dom.window.onpopstate = popState => {

      val split = dom.window.location.href.split('#').tail.headOption match {
        case None       => navigate("")
        case Some(page) => navigate(page)
      }

      println(dom.window.location.href)
    }

    def designPage(subPage: String) = {
      <div>
        <div class="span3">
          { sideBar(subPage) }
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

    val layout =
      <div>
        {
          page.map(p => topNavigationBar(p.split('/').head))
        }
        <div class="container-fluid">
          <div class="row-fluid">
            {
              page.map { name =>

                val subPage = name.split('/').tail.headOption.getOrElse("")

//                println("subpage: " + subPage)

                designPage(subPage)
              }
            }
          </div>
          <hr />
          <footer>
            <p>Company 2013</p>
          </footer>
        </div>
      </div>

    val div = dom.document.getElementById("scalaJsContent")

    mount(div, layout)
  }
}
