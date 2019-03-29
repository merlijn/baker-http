package com.github.merlijn.baker.api

import com.github.merlijn.baker.model
import com.github.merlijn.baker.model._
import com.ing.baker.recipe.javadsl
import com.ing.baker.runtime.core.{ProcessEvent, ProcessState, SensoryEventStatus}
import com.ing.baker.types._
import io.circe.{Decoder, Encoder, HCursor, Json}

object DSLJsonCodecs extends model.JsonCodecs {

  import io.circe.generic.semiauto._

  def parseJson(json: io.circe.Json): Value = {
    if (json.isNull)
      NullValue
    else if (json.isBoolean)
      PrimitiveValue(json.asBoolean.get)
    else if (json.isString)
      PrimitiveValue(json.asString.get)
    else if (json.isObject) {
      val fields: Iterable[(String, Value)] = json.asObject.get.toIterable.map {
        case (name, value) => name -> parseJson(value)
      }
      RecordValue(fields.toMap)
    }
    else
      throw new IllegalArgumentException("Invalid json")
  }

  def parseSchema(schema: Schema): Type = schema.`type` match {
    case "string"  => CharArray
    case "integer" => Int64
    case "boolean" => Bool
    case "object" => {

      val fields = schema.properties.get.map {
        case (name, schema) => RecordField(name, parseSchema(schema))
      }.toSeq

      RecordType(fields)
    }
  }

  def parseEvent(e: Event): javadsl.Event = {
    val ingredients = e.providedIngredients.map { case (name, schema) => javadsl.Ingredient(name, parseSchema(schema))  }.toSeq
    javadsl.Event(e.name, ingredients, None)
  }

  def parseInteraction(i: Interaction) = {
    javadsl.Interaction(
      name = i.name,
      input = i.input.map { case (name, schema) => javadsl.Ingredient(name, parseSchema(schema)) }.toSeq,
      output = i.output.map(parseEvent),
      requiredEvents = i.requiredEvents.map(_.toSet).getOrElse(Set.empty)
    )
  }

  def parseRecipe(recipe: Recipe): javadsl.Recipe = {
    val sensoryEvents = recipe.sensoryEvents.map { parseEvent }
    val interactions = recipe.interactions.map { parseInteraction }

    javadsl.Recipe(
      name = recipe.name,
      interactions = interactions,
      sensoryEvents = sensoryEvents)
  }

  implicit val valueDecoder: Decoder[Value] = (c: HCursor) => Right(parseJson(c.value))
  implicit val valueEncoder: Encoder[Value] = {
    case PrimitiveValue(b: Boolean) => Json.fromBoolean(b)
    case PrimitiveValue(i: Integer) => Json.fromInt(i)
    case PrimitiveValue(s: String)  => Json.fromString(s)
    case RecordValue(records) =>
      val fields: Seq[(String, Json)] = records.toSeq.map {
        case (name, value) => name -> Json.Null
      }
      Json.obj(fields: _*)
  }

  implicit val processEventDecoder: Decoder[ProcessEvent] = deriveDecoder[ProcessEvent]
  implicit val processEventEncoder: Encoder[ProcessEvent] = deriveEncoder[ProcessEvent]

  implicit val processStateDecoder: Decoder[ProcessState] = deriveDecoder[ProcessState]

  implicit val interactionDSLEncoder: Encoder[javadsl.Interaction] = JsonCodecs.interactionEncoder.contramap { i =>
    model.Interaction(i.name, None, Map.empty, Seq.empty, None, None)
  }

  implicit val recipeDSLDecoder: Decoder[javadsl.Recipe] = JsonCodecs.recipeDecoder.emap { recipe =>
    Right(parseRecipe(recipe))
  }

  implicit val sensoryEventStatusEncoder: Encoder[SensoryEventStatus] = (a: SensoryEventStatus) => Json.fromString(a.name())
}
