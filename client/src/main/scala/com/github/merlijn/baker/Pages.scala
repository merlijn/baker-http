package com.github.merlijn.baker

import com.github.merlijn.baker.Components.{Menu, interactionsTable}
import com.github.merlijn.baker.model.Interaction
import mhtml.{Rx, Var}

import scala.xml.Elem

object Pages {

  val noContent: Rx[Elem] = Var(<div></div>)

  lazy val interactions: Var[Seq[Interaction]] = {

    val seq: Var[Seq[Interaction]] = Var(Seq.empty)
    API.getInteractions(seq := _)
    seq
  }

  def cataloguePage(subPage: String): Elem = {
    <div>
      <div class="span3">
        { Menu("catalogue", Seq("Recipes", "Interactions", "Ingredients"), subPage) }
      </div>
      <div class="span9">
        <div class="row-fluid">
          {
            if (subPage == "interactions")
              interactions.map(interactionsTable)
            else
              noContent
          }
        </div>
      </div>
    </div>
  }

  def monitorPage(subPage: String): Elem =
    <div>
      <div class="span3">
        { Menu("interact", Seq("Activity", "Failures", "Graphs"), subPage) }
      </div>
      <div class="span9">
        <div class="row-fluid">
          {
            noContent
          }
        </div>
      </div>
    </div>
}
