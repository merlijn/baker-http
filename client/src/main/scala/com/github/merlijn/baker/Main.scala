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

    val interactionVar: Var[Seq[Interaction]] = Var(Seq.empty)

    def navigate(dest: String): Unit = dest match {
      case "interactions" =>

        dom.ext.Ajax.get("/catalogue/interactions").onComplete {
          case Success(xhr) =>

            val json = xhr.responseText
            interactionVar := decodeUnsafe[Seq[Interaction]](json)
            page := dest

          case Failure(e) =>
            System.err.println(e.toString)
        }
      case other =>
        page := dest
    }

    val layout =
      <div>
        { navBar }
        <div class="container-fluid">
          <div class="row-fluid">
            {
              page.map { name =>
                <div>
                <div class="span3">
                  { sideBar(name, navigate _) }
                </div>
                <div class="span9">
                  <div class="row-fluid">
                    {
                      if (name == "interactions")
                        interactionVar.map(renderInteraction)
                      else
                        noContent
                    }
                  </div>
                </div>
                </div>
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
