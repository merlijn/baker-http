package com.github.merlijn.baker.shared

case class Interaction(name: String,
                       originalName: Option[String],
                       input: Seq[Ingredient],
                       output: Seq[Event],
                       maximumExecutionCount: Option[Int] = None)
