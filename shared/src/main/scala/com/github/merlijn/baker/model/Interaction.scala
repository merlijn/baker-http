package com.github.merlijn.baker.model

case class Interaction(name: String,
                       originalName: Option[String],
                       input: Map[String, Schema],
                       output: Seq[Event],
                       requiredEvents: Option[Seq[String]],
                       maximumExecutionCount: Option[Int] = None)
