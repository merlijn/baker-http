package com.github.merlijn.baker.api

import akka.http.scaladsl.marshalling.{PredefinedToEntityMarshallers, ToEntityMarshaller}
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, PredefinedFromEntityUnmarshallers}
import com.github.merlijn.baker.model.JsonCodecs._
import guru.nidi.graphviz.engine.Graphviz
import io.circe._
import scalatags.Text

object EntityMarshalling {

  implicit val scalaTagsMarshaller: ToEntityMarshaller[Text.TypedTag[String]] =
    PredefinedToEntityMarshallers.stringMarshaller(`text/html`).compose {
      case html if html.tag == "html" => s"<!DOCTYPE html>${html.render}"
      case tag                        => tag.render
    }

  implicit def graphVizMarshaller: ToEntityMarshaller[Graphviz] = PredefinedToEntityMarshallers.byteArrayMarshaller(`image/svg+xml`).compose { graph =>

    import guru.nidi.graphviz.engine.Format

    graph.render(Format.SVG).toString.getBytes
  }

  implicit def jsonUnMarshaller[T : Decoder]: FromEntityUnmarshaller[T] = PredefinedFromEntityUnmarshallers
    .stringUnmarshaller.forContentTypes(`application/json`)
    .map { rawJson => decodeUnsafe[T](rawJson) }

  implicit def jsonMarshaller[T : Encoder]: ToEntityMarshaller[T] = PredefinedToEntityMarshallers
    .stringMarshaller(`application/json`)
    .compose { obj => implicitly[Encoder[T]].apply(obj).toString() }
}
