package com.github.merlijn.baker.api

import scalatags.Text
import scalatags.Text.all._
import scalatags.Text.tags2

object Html {

  val index: Text.TypedTag[String] = {

    html(
      head(
        link(rel := "stylesheet", href := "/assets/css/app.css"),
        link(rel := "stylesheet", href := "/assets/css/bootstrap.css"),
        script(src := "/assets/js/jquery.js"),
        script(src := "/assets/js/bootstrap.js"),
        tags2.title("Baker")
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
