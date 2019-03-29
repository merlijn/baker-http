package com.github.merlijn.baker.model

case class Interaction(name: String,
                       originalName: Option[String],
                       input: Map[String, Schema],
                       output: Seq[Event],
                       maximumExecutionCount: Option[Int] = None)
