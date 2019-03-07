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
        ul(
          li("Akka HTTP shouts out: ", em(message)),
          li("Scala.js shouts out: ", em(id := "scalajsShoutOut"))
        ),
        raw(
          scalajs.html.scripts(
            "client",
            name => s"/assets/$name",
            name => getClass.getResource(s"/public/$name") != null)
          .body)
      )
    )
  }
}
