package com.github.merlijn.baker

import com.github.merlijn.baker.model.{Interaction, LogItem, Recipe}
import com.github.merlijn.baker.model.JsonCodecs._
import io.circe.{Decoder, Encoder}
import org.scalajs.dom

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.util.{Failure, Success}

object API {

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
    get[Seq[Interaction]]("api/catalogue/interactions", callbackFn)
  }

  def getRecipes(callbackFn: Seq[Recipe] => Unit): Unit = {
    get[Seq[Recipe]]("api/catalogue/recipes", callbackFn)
  }

  def getLogs(sort: String, limit: Int)(callbackFn: Seq[LogItem] => Unit): Unit = {
    get[Seq[LogItem]]("api/log", callbackFn)
  }
}
