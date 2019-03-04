package com.github.merlijn.baker.api

import akka.http.scaladsl.marshalling.{Marshaller, PredefinedToEntityMarshallers, ToEntityMarshaller}
import akka.http.scaladsl.model.MediaTypes.{`application/json`, `text/html`, `text/plain`, `text/xml`}
import akka.http.scaladsl.model.{HttpEntity, MediaType}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, PredefinedFromEntityUnmarshallers, Unmarshaller}
import com.github.merlijn.baker.shared.Recipe
import com.ing.baker.runtime.core.{ProcessEvent, ProcessState, SensoryEventStatus}
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

  import net.liftweb.json._
  import net.liftweb.json.Serialization._
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

  implicit val recipeUnmarshaller = jsonUnMarshaller[Recipe]
}
