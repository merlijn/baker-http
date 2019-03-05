package com.github.merlijn.baker.api

import akka.http.scaladsl.marshalling.{Marshaller, PredefinedToEntityMarshallers, ToEntityMarshaller}
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.{HttpEntity, MediaType}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, PredefinedFromEntityUnmarshallers, Unmarshaller}
import com.github.merlijn.baker.shared.{Event, Ingredient, Recipe, Schema}
import com.ing.baker.recipe.javadsl
import com.ing.baker.runtime.core.{ProcessEvent, ProcessState, SensoryEventStatus}
import com.ing.baker.types._
import guru.nidi.graphviz.engine.Graphviz
import play.twirl.api.{Html, Txt, Xml}
import scalatags.Text

trait EntityMarshalling {

  implicit val ScalaTagsMarshaller: ToEntityMarshaller[Text.TypedTag[String]] =
    PredefinedToEntityMarshallers.stringMarshaller(`text/html`).compose {
      case html if html.tag == "html" =>
        println("html")
        s"<!DOCTYPE html>${html.render}"
      case tag => tag.render
    }

  /** Twirl marshallers for Xml, Html and Txt mediatypes */
  implicit val twirlHtmlMarshaller = twirlMarshaller[Html](`text/html`)
  implicit val twirlTxtMarshaller  = twirlMarshaller[Txt](`text/plain`)
  implicit val twirlXmlMarshaller  = twirlMarshaller[Xml](`text/xml`)

  def twirlMarshaller[A](contentType: MediaType): ToEntityMarshaller[A] = {
    Marshaller.StringMarshaller.wrap(contentType)(_.toString)
  }

  import net.liftweb.json.Serialization._
  import net.liftweb.json._
  implicit val formats = DefaultFormats

  def jsonUnMarshaller[T : Manifest]: FromEntityUnmarshaller[T] = PredefinedFromEntityUnmarshallers.stringUnmarshaller.map { string =>
    read[T](string)
  }

  def jsonMarshaller[T : Manifest]: ToEntityMarshaller[T] = PredefinedToEntityMarshallers.stringMarshaller(`application/json`).compose { obj =>
    write(obj)
  }

  implicit val processStateUnMarshaller = jsonUnMarshaller[ProcessState]
  implicit val eventUnmarshaller = jsonUnMarshaller[ProcessEvent]
  implicit val eventListUnmarshaller = jsonUnMarshaller[List[ProcessEvent]]

  implicit val sensoryEventStatusMarhaller = jsonMarshaller[SensoryEventStatus]

  implicit val ingredientsMarhaller = jsonMarshaller[Map[String, Any]]
  implicit val eventMarshaller = jsonMarshaller[ProcessEvent]
  implicit val eventListMarshaller = jsonMarshaller[List[ProcessEvent]]
  implicit val stringMarshaller = jsonMarshaller[String]

  implicit def graphVizMarshaller: ToEntityMarshaller[Graphviz] = PredefinedToEntityMarshallers.byteArrayMarshaller(`image/svg+xml`).compose { graph =>

    import guru.nidi.graphviz.engine.Format

    graph.render(Format.SVG).toString.getBytes
  }

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
