package com.github.merlijn.baker.model

import io.circe._
import io.circe.generic.semiauto._

object JsonCodecs extends JsonCodecs {

}

trait JsonCodecs {

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

  implicit val logItemDecoder: Decoder[LogItem] = deriveDecoder
  implicit val logItemEncoder: Encoder[LogItem] = deriveEncoder

  def decodeUnsafe[T : Decoder](json: String): T = {
    val result = io.circe.parser.parse(json)
      .map(resolveJsonLinks)
      .flatMap(json => implicitly[Decoder[T]].decodeJson(json))

    result match {
      case Left(exception) => throw exception
      case Right(result) => result
    }
  }

  def getJsonLink(json: Json, path: Seq[String]): Either[ParsingFailure, Json] = {

    if (path.isEmpty)
      Right(json)
    else {
      json.arrayOrObject(
        {
          val msg = "Failed to resolve link: " + path.mkString("/")
          val exception = new IllegalStateException()
          Left(ParsingFailure.apply(msg, exception))
        },
        { array =>
          val index = path.head.toInt
          getJsonLink(array.apply(index), path.tail)
        },
        { obj =>
          val key = path.head
          getJsonLink(obj.apply(key).get, path.tail)
        }
      )
    }
  }

  def resolveJsonLinks(json: Json): Json = resolveJsonLinks(json, json)

  def resolveJsonLinks(root: Json, json: Json): Json = {
    json.arrayOrObject[Json](
      json,
      { array =>
        Json.arr(array.map(resolveJsonLinks(root, _)): _*)
      },
      { obj =>

        obj
          .apply("$ref")
          .filter(_ => obj.size == 1)
          .flatMap(_.asString)
          .map { value =>
            val path = value.split(Array('/', '#')).filter(_.nonEmpty)
            getJsonLink(root, path).right.get
          }.getOrElse {
          val entries = obj.mapValues(resolveJsonLinks(root, _))
          Json.obj(entries.toList: _*)
        }
      }
    )
  }

  implicit def seqEncoder[T : Encoder]: Encoder[Seq[T]] = new ArrayEncoder[Seq[T]] {
    override def encodeArray(a: Seq[T]): Vector[Json] = a.map(e => implicitly[Encoder[T]].apply(e)).toVector
  }

  implicit def seqDecoder[T : Decoder]: Decoder[Seq[T]] = io.circe.Decoder.decodeSeq[T]
}
