package com.github.merlijn.baker.model

case class Recipe(name: String,
                  label: String,
                  types: Map[String, Schema],
                  sensoryEvents: Seq[Event],
                  interactions: Seq[Interaction])
