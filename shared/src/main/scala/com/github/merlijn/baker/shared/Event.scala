package com.github.merlijn.baker.shared

case class Event(name: String, label: Option[String], providedIngredients: List[Ingredient])
