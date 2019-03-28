package com.github.merlijn.baker.pages

import com.github.merlijn.baker.model.Interaction
import com.github.merlijn.baker.{API, Components, components}
import mhtml.Var

import scala.xml.Elem

object Catalogue {

  lazy val interactions: Var[Seq[Interaction]] = {

    val seq: Var[Seq[Interaction]] = Var(Seq.empty)
    API.getInteractions(seq := _)
    seq
  }

  def interactionsTable(interactions: Seq[Interaction]) =
    components.Table[Interaction](
      interactions,
      Seq(
        "Name"   -> (i => <td><a href = { s"#catalogue/interactions/${i.name}" }>{ i.name }</a></td>),
        "Input"  -> (i => <td>{ i.input.map(_.name).mkString(",") } </td> ),
        "Output" -> (i => <td>{ i.output.map(_.name).mkString(",") } </td>)
      )
    )

  def apply(subPage: String): Elem = {
    <div>
      <div class="span3">
        { components.Menu("catalogue", Seq("Recipes", "Interactions", "Ingredients"), subPage) }
      </div>
      <div class="span9">
        <div class="row-fluid">
          {
            subPage match {
              case "interactions" => interactions.map(interactionsTable)
              case _              => Components.noContent
            }
          }
        </div>
      </div>
    </div>
  }
}
