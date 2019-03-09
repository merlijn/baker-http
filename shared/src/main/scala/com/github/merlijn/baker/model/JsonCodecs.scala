package com.github.merlijn.baker.model

import io.circe.{ArrayEncoder, Decoder, Encoder, Json}
import io.circe.generic.semiauto._

object JsonCodecs {

  implicit val eventDecoder: Decoder[Event] = deriveDecoder
  implicit val eventEncoder: Encoder[Event] = deriveEncoder

  implicit val schemaDecoder: Decoder[Schema] = deriveDecoder
  implicit val schemaEncoder: Encoder[Schema] = deriveEncoder

  implicit val ingredientDecoder: Decoder[Ingredient] = deriveDecoder
  implicit val ingredientEncoder: Encoder[Ingredient] = deriveEncoder

  implicit val interactionDecoder: Decoder[Interaction] = deriveDecoder
  implicit val interactionEncoder: Encoder[Interaction] = deriveEncoder

  implicit val recipeDecoder: Decoder[Recipe] = deriveDecoder
  implicit val recipeEncoder: Encoder[Recipe] = deriveEncoder

  def decodeUnsafe[T : Decoder](json: String): T = {
    val result = io.circe.parser.parse(json).flatMap(jsonAST => implicitly[Decoder[T]].decodeJson(jsonAST))
    result.right.get
  }

  implicit def seqEncoder[T : Encoder]: Encoder[Seq[T]] = new ArrayEncoder[Seq[T]] {
    override def encodeArray(a: Seq[T]): Vector[Json] = a.map(e => implicitly[Encoder[T]].apply(e)).toVector
  }

  implicit def seqDecoder[T : Decoder]: Decoder[Seq[T]] = io.circe.Decoder.decodeSeq[T]
}
