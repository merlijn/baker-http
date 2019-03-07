package com.github.merlijn.baker.api

import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, PredefinedFromEntityUnmarshallers}
import com.github.merlijn.baker.shared._
import io.circe.Decoder
import io.circe.generic.semiauto._

object CirceCodecs {

//  implicit val typeDecoder: Decoder[Type] = (c: HCursor) => for {
//    typeName <- c.downField("foo").as[String]
//  } yield {
//    typeName match {
//      case "string"  => CharArray
//      case "integer" => Int64
//      case "boolean" => Bool
//      case _ => throw new IllegalStateException("unsupported type")
//    }
//  }
//
//  implicit val ingredientDecoder: Decoder[javadsl.Ingredient] = (c: HCursor) => for {
//    name           <- c.downField("name").as[String]
//    ingredientType <- c.downField("schema").as[Type]
//  } yield {
//    javadsl.Ingredient(name, ingredientType)
//  }
//
//  implicit val eventDecoder: Decoder[javadsl.Event] = (c: HCursor) => for {
//    name           <- c.downField("name").as[String]
//    ingredients    <- c.downField("ingredients").as[List[javadsl.Ingredient]]
//  } yield {
//    javadsl.Event(name, ingredients, None)
//  }
//
//  implicit val interactionDecoder: Decoder[javadsl.Interaction] = (c: HCursor) => for {
//    name           <- c.downField("name").as[String]
//    input          <- c.downField("input").as[List[javadsl.Ingredient]]
//    output         <- c.downField("output").as[List[javadsl.Event]]
//  } yield {
//    javadsl.Interaction(name, input, output)
//  }

  // derived decoders
  implicit val eventDecoder: Decoder[Event] = deriveDecoder
  implicit val schemaDecoder: Decoder[Schema] = deriveDecoder
  implicit val ingredientDecoder: Decoder[Ingredient] = deriveDecoder
  implicit val interactionDecoder: Decoder[Interaction] = deriveDecoder
  implicit val recipeDecoder: Decoder[Recipe] = deriveDecoder

  implicit def jsonCirceUnMarshaller[T : Decoder]: FromEntityUnmarshaller[T] = PredefinedFromEntityUnmarshallers.stringUnmarshaller.map { rawJson =>

    import io.circe.parser._

    val foo: Either[io.circe.Error, T] = parse(rawJson).flatMap(jsonAST => implicitly[Decoder[T]].decodeJson(jsonAST))

    foo.right.get
  }
}
