package com.github.merlijn.baker.api.html

import scalatags.Text
import scalatags.Text._
import scalatags.Text.all._

object Pages {

  def index(message: String) = main("title") {
    ul(
      li("Akka HTTP shouts out: ", em(message)),
      li("Scala.js shouts out: ", em(id := "scalajsShoutOut"))
    )
  }

  def main(_title: String)(_content: Frag): Text.TypedTag[String] = {

    html(
      head(
        tags2.title(_title)
      ),
      body(
        _content,
        raw(
          scalajs.html.scripts(
            "client",
            name => s"/assets/$name",
            name => getClass.getResource(s"/public/$name") != null).body)
      )
    )
  }
}
