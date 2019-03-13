package com.github.merlijn.baker.api

import com.github.merlijn.baker.model
import com.github.merlijn.baker.model.{Event, Ingredient, JsonCodecs, Schema}
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
    model.Interaction(i.name, None, Seq.empty, Seq.empty, None)
  }

  implicit val recipeDSLDecoder: Decoder[javadsl.Recipe] = JsonCodecs.recipeDecoder.emap { recipe =>

    def parseIngredient(i: Ingredient): javadsl.Ingredient = {
      javadsl.Ingredient(i.name, parseSchema(i.schema))
    }

    def parseEvent(e: Event): javadsl.Event = {
      val ingredients = e.providedIngredients.map { parseIngredient  }
      javadsl.Event(e.name, ingredients, None)
    }

    val sensoryEvents = recipe.sensoryEvents.map { parseEvent }

    val interactions = recipe.interactions.map { i =>
      javadsl.Interaction(
        name = i.name,
        input = i.input.map(parseIngredient),
        output = i.output.map(parseEvent)
      )
    }

    Right(javadsl.Recipe(
      name = recipe.name,
      interactions = interactions,
      sensoryEvents = sensoryEvents))
  }

  implicit val sensoryEventStatusEncoder: Encoder[SensoryEventStatus] = (a: SensoryEventStatus) => Json.fromString(a.name())
}
