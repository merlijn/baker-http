package com.github.merlijn.baker.pages

import com.github.merlijn.baker.model.LogItem
import com.github.merlijn.baker.{API, Components, Util, components}
import mhtml.Var

import scala.xml.Elem

object Monitor {

  lazy val logs: Var[Seq[LogItem]] =
    Util.initVarFromCallback(Seq.empty, API.getLogs(sort = "time", limit = 10))

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
        "Recipe"      -> (i => <span>{ i.recipe } </span>),
        "Process Id"  -> (i => <span class="monospace">{ i.processId } </span> ),
        "Type"        -> (i => <span>{ i.eventType } </span>),
        "Interaction" -> (i => <span>{ i.interaction } </span>),
        "Event"       -> (i => <span>{ i.event } </span>)
      ),
      i => Some(i.style)
    )
}
