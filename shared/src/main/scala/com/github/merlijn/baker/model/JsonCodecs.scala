package com.github.merlijn.baker.model

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

object JsonCodecs {

  // derived decoders
  implicit val eventDecoder: Decoder[Event] = deriveDecoder
  implicit val schemaDecoder: Decoder[Schema] = deriveDecoder
  implicit val ingredientDecoder: Decoder[Ingredient] = deriveDecoder
  implicit val interactionDecoder: Decoder[Interaction] = deriveDecoder
  implicit val recipeDecoder: Decoder[Recipe] = deriveDecoder
}
