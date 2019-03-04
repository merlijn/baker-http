package com.github.merlijn.baker.model

import com.ing.baker.types.Type

case class Ingredient(name: String, schema: Schema) {

  val ingredientType: Type = schema.toType
}
