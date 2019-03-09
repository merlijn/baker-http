package com.github.merlijn.baker.api

import akka.http.scaladsl.marshalling.{PredefinedToEntityMarshallers, ToEntityMarshaller}
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, PredefinedFromEntityUnmarshallers, Unmarshaller}
import com.github.merlijn.baker.model.JsonCodecs._
import com.github.merlijn.baker.model
import com.github.merlijn.baker.model._
import com.ing.baker.recipe.javadsl
import com.ing.baker.runtime.core.{ProcessEvent, ProcessState, SensoryEventStatus}
import com.ing.baker.types._
import guru.nidi.graphviz.engine.Graphviz
import io.circe._
import scalatags.Text

object EntityMarshalling {

  implicit val scalaTagsMarshaller: ToEntityMarshaller[Text.TypedTag[String]] =
    PredefinedToEntityMarshallers.stringMarshaller(`text/html`).compose {
      case html if html.tag == "html" => s"<!DOCTYPE html>${html.render}"
      case tag                        => tag.render
    }

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

  implicit val sensoryEventStatusEncoder: Encoder[SensoryEventStatus] = (a: SensoryEventStatus) => Json.fromString(a.name())


  implicit def graphVizMarshaller: ToEntityMarshaller[Graphviz] = PredefinedToEntityMarshallers.byteArrayMarshaller(`image/svg+xml`).compose { graph =>

    import guru.nidi.graphviz.engine.Format

    graph.render(Format.SVG).toString.getBytes
  }

  implicit def jsonUnMarshaller[T : Decoder]: FromEntityUnmarshaller[T] = PredefinedFromEntityUnmarshallers.stringUnmarshaller.map { rawJson =>

    decodeUnsafe[T](rawJson)
  }

  implicit def jsonMarshaller[T : Encoder]: ToEntityMarshaller[T] = PredefinedToEntityMarshallers.stringMarshaller(`application/json`).compose { obj =>
    implicitly[Encoder[T]].apply(obj).toString()
  }

  // akka marshalling

  implicit val processStateUnMarshaller = jsonUnMarshaller[ProcessState]
  implicit val eventUnmarshaller = jsonUnMarshaller[ProcessEvent]
  implicit val eventMarshaller = jsonMarshaller[ProcessEvent]
  implicit val eventListUnmarshaller = jsonUnMarshaller[List[ProcessEvent]]
  implicit val eventListMarshaller = jsonMarshaller[List[ProcessEvent]]
  implicit val recipeListMarshaller = jsonMarshaller[Seq[model.Recipe]]
  implicit val InteractionSeqMarshaller = jsonMarshaller[Seq[model.Interaction]]
  implicit val ingredientsMarhaller = jsonMarshaller[Map[String, Value]]
  implicit val sensoryEventStatusMarhaller = jsonMarshaller[SensoryEventStatus]

  implicit val recipeUnmarshaller: Unmarshaller[HttpEntity, javadsl.Recipe] = jsonUnMarshaller[Recipe].map { recipe =>

    def parseType(schema: Schema): Type = schema.`type` match {
      case "string"  => CharArray
      case "integer" => Int64
      case "boolean" => Bool
      case "object" => {

        val fields = schema.properties.get.map {
          case (name, schema) => RecordField(name, parseType(schema))
        }.toSeq

        RecordType(fields)
      }
    }

    def parseIngredient(i: Ingredient): javadsl.Ingredient = {
      javadsl.Ingredient(i.name, parseType(i.schema))
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

    javadsl.Recipe(
      name = recipe.name,
      interactions = interactions,
      sensoryEvents = sensoryEvents)
  }
}
