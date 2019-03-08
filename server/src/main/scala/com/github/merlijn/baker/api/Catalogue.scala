package com.github.merlijn.baker.api

import com.github.merlijn.baker.model.{Interaction, Recipe}

class Catalogue {

  def allInteractions(): Set[Interaction] = allRecipes().flatMap(_.interactions).toSet

  def allRecipes(): Seq[Recipe] = ???
}
