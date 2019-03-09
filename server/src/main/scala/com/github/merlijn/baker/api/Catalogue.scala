package com.github.merlijn.baker.api

import com.github.merlijn.baker.model.{Interaction, Recipe}

import com.github.merlijn.baker.api.EntityMarshalling._

import com.github.merlijn.baker.model.JsonCodecs._

class Catalogue {

  def allInteractions(): Seq[Interaction] = allRecipes().flatMap(_.interactions).toSet.toSeq

  def allRecipes(): Seq[Recipe] = {

    val is = classOf[Catalogue].getClassLoader().getResourceAsStream("./catalogue/recipes/webshop.json")

    val json = scala.io.Source.fromInputStream(is).mkString

    val webshop = decodeUnsafe[Recipe](json)

    Seq(webshop)
  }
}
