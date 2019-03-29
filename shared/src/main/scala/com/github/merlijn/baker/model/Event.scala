package com.github.merlijn.baker.model

case class Event(name: String, label: Option[String], providedIngredients: Map[String, Schema])
