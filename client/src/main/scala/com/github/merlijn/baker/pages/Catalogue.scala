package com.github.merlijn.baker.pages

import com.github.merlijn.baker.model.{Interaction, Recipe}
import com.github.merlijn.baker.{API, Components, Util, components}
import mhtml.Var

import scala.xml.Elem

object Catalogue {

  lazy val interactions: Var[Seq[Interaction]] = Util.initVarFromCallback(Seq.empty, API.getInteractions _)
  lazy val recipes: Var[Seq[Recipe]] = Util.initVarFromCallback(Seq.empty, API.getRecipes _)

  def interactionsTable(interactions: Seq[Interaction]) =
    components.Table[Interaction](
      interactions,
      Seq(
        "Name"   -> (i => <a href = { s"#catalogue/interactions/${i.name}" }>{ i.name }</a>),
        "Input"  -> (i => <span>{ i.input.map(_.name).mkString(",") } </span> ),
        "Output" -> (i => <span>{ i.output.map(_.name).mkString(",") } </span>)
      )
    )

  def recipeTable(recipes: Seq[Recipe]) =
    components.Table[Recipe](
      recipes,
      Seq(
        "Name"               -> (i => <span><a href = { s"#catalogue/recipes/${i.name}" }>{ i.name }</a></span>),
        "Interaction count"  -> (i => <span>{ i.sensoryEvents.size } </span> ),
        "Event count"        -> (i => <span>{ i.sensoryEvents.size } </span>)
      )
    )

  def recipeImage(name: String) = {

    val imgageSource = s"/api/catalogue/recipe/svg/$name"

    <img src= { imgageSource } />
  }

  def apply(path: Seq[String]): Elem = {

    val subPage = path.headOption.getOrElse("recipes")

    <div>
      <div class="span3">
        { components.Menu("catalogue", Seq("Recipes", "Interactions", "Ingredients"), subPage) }
      </div>
      <div class="span9">
        <div class="row-fluid">
          {
            subPage match {
              case "interactions" =>
                interactions.map(interactionsTable)
              case "recipes" =>
                if (path.size == 2)
                  Var(recipeImage(path(1)))
                else
                  recipes.map(recipeTable)
              case _              =>
                Components.noContent
            }
          }
        </div>
      </div>
    </div>
  }
}
