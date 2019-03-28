package com.github.merlijn.baker

import com.github.merlijn.baker.model.{Interaction, LogItem, Recipe}
import com.github.merlijn.baker.model.JsonCodecs._
import io.circe.{Decoder, Encoder}
import org.scalajs.dom

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.util.{Failure, Success}

object API {

  val testLogs = Seq(
    LogItem("log-info", "Webshop", java.util.UUID.randomUUID().toString, "Event received", "", "OrderPlaced"),
    LogItem("log-info", "Webshop", java.util.UUID.randomUUID().toString, "Event received", "", "OrderPlaced"),
    LogItem("log-error", "Webshop", java.util.UUID.randomUUID().toString, "Interaction failed", "ValidateOrder", ""),
    LogItem("log-warn", "Webshop", java.util.UUID.randomUUID().toString, "Retry scheduled in 10 seconds", "ValidateOrder", "")
  )

  def get[T : Decoder](path: String, callback: T => Unit): Unit = {
    dom.ext.Ajax.get(path).onComplete {
      case Success(xhr) =>
        val json: String = xhr.responseText
        val decoded: T = decodeUnsafe[T](json)
        callback(decoded)
      case Failure(e)   =>
        System.err.println(e.toString)
    }
  }

//  def post[T : Encoder](path: String, callbackFn: () => Unit) = {
//    dom.ext.Ajax.post(path).onComplete {
//      case Success(xhr) =>
//        val json: String = xhr.responseText
//        val decoded: T = decodeUnsafe[T](json)
//        callback(decoded)
//      case Failure(e)   =>
//        System.err.println(e.toString)
//    }
//  }

  def getInteractions(callbackFn: Seq[Interaction] => Unit): Unit = {
    get[Seq[Interaction]]("catalogue/interactions", callbackFn)
  }

  def getLogs(sort: String, limit: Int, callbackFn: Seq[LogItem] => Unit): Unit = {
    callbackFn(testLogs)
  }
}
