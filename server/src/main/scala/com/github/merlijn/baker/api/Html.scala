package com.github.merlijn.baker.api

import scalatags.Text
import scalatags.Text.all._
import scalatags.Text.tags2

object Html {

  val index: Text.TypedTag[String] = {

    html(
      head(
        link(rel := "stylesheet", href := "/resources/css/app.css"),
        link(rel := "stylesheet", href := "/resources/css/bootstrap.css"),
        script(src := "/resources/js/jquery.js"),
        script(src := "/resources/js/bootstrap.js"),
        tags2.title("title")
      ),
      body(`class` := "page")(
        div(id := "scalaJsContent"),
        raw(
          scalajs.html.scripts(
            "baker-http-client",
            name => s"/js/$name",
            name => getClass.getResource(s"/js/$name") != null)
          .body.trim)
      )
    )
  }
}
