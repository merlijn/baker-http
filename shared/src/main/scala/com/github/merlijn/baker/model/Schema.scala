package com.github.merlijn.baker.model

case class Schema(`type`: String, properties: Option[Map[String, Schema]])