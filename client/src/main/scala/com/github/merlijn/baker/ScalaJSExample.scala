package com.github.merlijn.baker

import com.github.merlijn.baker.model.Interaction
import com.github.merlijn.baker.model.JsonCodecs._
import io.circe.Decoder
import org.scalajs.dom
import scalatags.JsDom.all._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.util.{Failure, Success}

object ScalaJSExample {

  def main(args: Array[String]): Unit = {

    dom.ext.Ajax.get("/catalogue/interactions").onComplete {
      case Success(xhr) =>

        val json = xhr.responseText

        val interactions = decodeUnsafe[Seq[Interaction]](json)

        dom.document.getElementById("scalaJsContent").appendChild(renderInteraction(interactions(0)).render)

      case Failure(e) => println(e.toString)

    }
  }

  def renderInteraction(interaction: model.Interaction) = {
    table(
      tr(
        td("name"), td(interaction.name)
      ),
      tr(
        td("input"), td(interaction.input.map(_.name).mkString(","))
      ),
      tr(
        td("output"), td(interaction.output.map(_.name).mkString(","))
      )
    )
  }
}
