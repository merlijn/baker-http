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
        script(src := "https://code.jquery.com/jquery-3.3.1.slim.min.js"),
        script(src := "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"),
        script(src := "https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"),
        tags2.title("Baker")
      ),
      body(`class` := "page")(
        div(id := "baker-app"),
        raw(
          scalajs.html.scripts(
            "baker-http-client",
            name => s"/assets/js/$name",
            name => getClass.getResource(s"/js/$name") != null).body.trim)
      )
    )
  }
}
