package com.github.merlijn.baker

import com.github.merlijn.baker.model.Interaction
import com.github.merlijn.baker.model.JsonCodecs._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.util.{Failure, Success}

object Main {

  def main(args: Array[String]): Unit = {

    import mhtml._
    import org.scalajs.dom

    val interactionVar: Var[Interaction] = Var(Interaction("", None, Seq.empty, Seq.empty, None))

    def getInteractions() = {
      dom.ext.Ajax.get("/catalogue/interactions").onComplete {
        case Success(xhr) =>

          val json = xhr.responseText
          val interactions = decodeUnsafe[Seq[Interaction]](json)

          interactionVar := interactions(0)

        case Failure(e) => println(e.toString)
      }
    }

    val component =
      <div>
        <button onclick = { () => getInteractions() }>Click me!</button>
        <table>
          <tr>
            <th>name</th>
            <th>input</th>
            <th>output</th>
          </tr>
          <tr>
            <td>{ interactionVar.map(_.name) }</td>
            <td>{ interactionVar.map{_.input.map(_.name).mkString(",") } } </td>
            <td>{ interactionVar.map{_.output.map(_.name).mkString(",") } } </td>
          </tr>
        </table>
      </div>

    val div = dom.document.getElementById("scalaJsContent")

    mount(div, component)
  }
}
