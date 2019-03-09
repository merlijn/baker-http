package com.github.merlijn.baker.api

import scalatags.Text
import scalatags.Text.all._
import scalatags.Text.tags2

object Html {

  def index(_title: String, message: String): Text.TypedTag[String] = {

    html(
      head(
        tags2.title(_title)
      ),
      body(
        h1("Hello"),
        div(id := "scalaJsContent"),
        raw(
          scalajs.html.scripts(
            "baker-http-client",
            name => s"/assets/$name",
            name => getClass.getResource(s"/public/$name") != null)
          .body.trim)
      )
    )
  }
}
