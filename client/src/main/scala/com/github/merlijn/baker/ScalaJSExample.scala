package com.github.merlijn.baker

import com.github.merlijn.baker.model.SharedMessages
import org.scalajs.dom

object ScalaJSExample {

  def main(args: Array[String]): Unit = {
    dom.document.getElementById("scalajsShoutOut").textContent = SharedMessages.itWorks
  }
}
