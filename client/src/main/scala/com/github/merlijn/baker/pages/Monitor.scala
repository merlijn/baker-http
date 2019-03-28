package com.github.merlijn.baker.pages

import com.github.merlijn.baker.model.LogItem
import com.github.merlijn.baker.{API, Components, components}
import mhtml.Var

import scala.xml.Elem

object Monitor {

  lazy val logs: Var[Seq[LogItem]] = {

    val seq: Var[Seq[LogItem]] = Var(Seq.empty)
    API.getLogs(sort = "time", limit = 10, callbackFn = data => seq := data)
    seq
  }

  def apply(subPage: String): Elem =
    <div>
      <div class="span3">
        { components.Menu("monitor", Seq("Activity", "Failures", "Graphs"), subPage) }
      </div>
      <div class="span9">
        <div class="row-fluid">
          {
            subPage match {
              case "activity" => logs.map(activityTable)
              case _          => Components.noContent
            }
          }
        </div>
      </div>
    </div>

  def activityTable(log: Seq[LogItem]): Elem =
    components.Table[LogItem](
      log,
      Seq(
        "Recipe"      -> (i => <td>{ i.recipe } </td>),
        "Process Id"  -> (i => <td class="monospace">{ i.processId } </td> ),
        "Type"        -> (i => <td>{ i.eventType } </td>),
        "Interaction" -> (i => <td>{ i.interaction } </td>),
        "Event"       -> (i => <td>{ i.event } </td>)
      ),
      i => Some(i.style)
    )
}
