package com.github.merlijn.baker

import com.github.merlijn.baker.Components.{Menu, interactionsTable}
import com.github.merlijn.baker.model.{Interaction, LogItem}
import mhtml.{Rx, Var}

import scala.xml.Elem

object Pages {

  val noContent: Rx[Elem] = Var(<div></div>)

  lazy val interactions: Var[Seq[Interaction]] = {

    val seq: Var[Seq[Interaction]] = Var(Seq.empty)
    API.getInteractions(seq := _)
    seq
  }

  lazy val logs: Var[Seq[LogItem]] = {

    val seq: Var[Seq[LogItem]] = Var(Seq.empty)
    API.getLogs(sort = "time", limit = 10, callbackFn = data => seq := data)
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
        { Menu("monitor", Seq("Activity", "Failures", "Graphs"), subPage) }
      </div>
      <div class="span9">
        <div class="row-fluid">
          {
            if (subPage == "activity")
              logs.map(activityTable)
            else
              noContent
          }
        </div>
      </div>
    </div>

  def activityTable(log: Seq[LogItem]): Elem = {
    <table class="table">
      <thead>
        <tr>
          <th scope="col">Recipe</th>
          <th scope="col">Process Id</th>
          <th scope="col">Type</th>
          <th scope="col">Interaction</th>
          <th scope="col">Event name</th>
        </tr>
      </thead>
      <tbody>
        {
        log.map { i =>
          <tr class= { i.style} >
            <td>{ i.recipe } </td>
            <td class="monospace">{ i.processId } </td>
            <td>{ i.eventType } </td>
            <td>{ i.interaction } </td>
            <td>{ i.event } </td>
          </tr>
        }
        }
      </tbody>
    </table>
  }



}
