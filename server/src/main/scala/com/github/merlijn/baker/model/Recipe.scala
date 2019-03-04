package com.github.merlijn.baker.model

import com.ing.baker.recipe.javadsl.{ Recipe => DSLRecipe }

case class Recipe(name: String, sensoryEvents: Seq[Event], interactions: Seq[Interaction]) {

  def toDSL: DSLRecipe = DSLRecipe(name)
}
