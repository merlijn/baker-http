package com.github.merlijn.baker.shared

case class Recipe(name: String, sensoryEvents: Seq[Event], interactions: Seq[Interaction])
