package com.github.merlijn.baker.model

case class Recipe(name: String, sensoryEvents: Seq[Event], interactions: Seq[Interaction])
