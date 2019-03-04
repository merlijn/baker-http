package com.github.merlijn.baker.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.FromEntityUnmarshaller
import akka.stream.{ActorMaterializer, Materializer}
import com.github.merlijn.baker.api.EntityMarshalling
import com.github.merlijn.baker.client.RemoteBaker._
import com.ing.baker.runtime.core.{ProcessEvent, ProcessState}
import com.ing.baker.types.Value
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{FiniteDuration, _}
import scala.concurrent.{Await, Future}

object RemoteBaker  {

  val log = LoggerFactory.getLogger(classOf[RemoteBaker])

  def doRequest[T](httpRequest: HttpRequest)(implicit actorSystem: ActorSystem, materializer: Materializer, timeout: FiniteDuration, um : FromEntityUnmarshaller[T]): T = {

    import akka.http.scaladsl.unmarshalling.Unmarshal

    log.info(s"Sending request: $httpRequest")

    val futureResult = Http().singleRequest(httpRequest).flatMap { response =>

      response match {
        case response@HttpResponse(StatusCodes.OK, _, _, _) =>

          Unmarshal(response).to[T]

        case resp@HttpResponse(code, _, _, _) =>
          resp.discardEntityBytes()
          log.error("Request failed with response code: " + code)
          Future.failed(throw new RuntimeException("Request failed with response code: " + code))
      }
    }

    Await.result(futureResult, timeout)
  }
}

class RemoteBaker(val host: String, val port: Int)(implicit val actorSystem: ActorSystem) extends EntityMarshalling {

  val baseUri = s"http://$host:$port"

  implicit val materializer = ActorMaterializer()
  implicit val requestTimeout: FiniteDuration = 30 seconds

//  def addRecipe(recipe: Recipe) : String = {
//
//    val serializedRecipe = serialize(recipe)
//
//    val httpRequest = HttpRequest(
//        uri = baseUri +  "/recipe",
//        method = akka.http.scaladsl.model.HttpMethods.POST,
//        entity = ByteString.fromArray(serializedRecipe))
//
//    doRequest[String](httpRequest)
//  }
//
//  def fireEvent(requestId: String, event: Any): SensoryEventStatus = {
//
//    //Create request to give to Baker
//    log.info("Creating runtime event to fire")
//    val processEvent = ProcessEvent.of(event)
//
//    val request = HttpRequest(
//        uri =  s"$baseUri/$requestId/fire-event?confirm=completed",
//        method = POST,
//        entity = ByteString.fromArray(defaultKryoPool.toBytesWithClass(processEvent)))
//
//    doRequest[SensoryEventStatus](request)
//  }

  def createProcessInstance(recipeId: String, requestId: String): Unit = {

    val request = HttpRequest(
        uri = s"$baseUri/$requestId/$recipeId/create-process",
        method = POST)

    doRequest[String](request)
  }

  def getState(requestId: String): ProcessState = {

    val request = HttpRequest(
        uri = s"$baseUri/$requestId/state",
        method = GET)

    doRequest[ProcessState](request)
  }

  def getIngredients(requestId: String): Map[String, Value] = getState(requestId).ingredients

  def getVisualState(requestId: String): String = {

    val request = HttpRequest(
      uri = s"$baseUri/$requestId/visual_state",
      method = GET)

    doRequest[String](request)
  }

  def getEvents(requestId: String): List[ProcessEvent] = {

    val request = HttpRequest(
      uri = s"$baseUri/$requestId/events",
      method = GET)

    doRequest[List[ProcessEvent]](request)
  }
}
