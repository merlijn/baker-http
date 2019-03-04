package com.github.merlijn.baker.api

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{Directives, Route}
import com.github.merlijn.baker.api.html.Pages
import com.github.merlijn.baker.shared.{Recipe, SharedMessages}
import com.ing.baker.compiler.RecipeCompiler
import com.ing.baker.runtime.core.{Baker, ProcessEvent}

import scala.concurrent.duration._

object BakerRoutes extends Directives with EntityMarshalling {

  implicit val timeout: FiniteDuration = 30 seconds

  val defaultEventConfirm = "receive"

  def apply(baker: Baker)(implicit actorSystem: ActorSystem): Route = {

    val testRoute = {
      pathSingleSlash {
        get {
          complete {

            Pages.index(SharedMessages.itWorks)
//            index.render(SharedMessages.itWorks)
          }
        }
      } ~
        pathPrefix("assets" / Remaining) { file =>
          // optionally compresses the response with Gzip or Deflate
          // if the client accepts compressed responses
          encodeResponse {
            getFromResource("public/" + file)
          }
        }
    }

    def processRoutes(processId: String): Route = {

      path("event") {
        post {
          (entity(as[ProcessEvent]) & parameter('confirm.as[String] ?)) { (event, confirm) =>

            val sensoryEventStatus = confirm.getOrElse(defaultEventConfirm) match {
              case "received" => baker.fireEventAsync(processId, event).confirmReceived()
              case "completed" => baker.fireEventAsync(processId, event).confirmCompleted()
              case other => throw new IllegalArgumentException(s"Unsupported confirm type: $other")
            }

            complete(sensoryEventStatus)
          }
        }
      } ~
        path("events") {
          get {
            val events = baker.getEvents(processId).toList
            complete(events)
          }
        } ~
        path(Segment / "create-process") { recipeId =>
          post {
            val processState = baker.createProcess(recipeId, processId)
            complete(processState.processId)
          }
        } ~
        path("ingredients") {
          get {

            val ingredients = baker.getIngredients(processId)

            complete(ingredients)
          }
        } ~
        path("visual_state") {
          get {

            val visualState = baker.getVisualState(processId)

            complete(visualState)
          }
        }
    }

    def recipeRoutes(): Route = path("compile") {
      post {
        entity(as[Recipe]) { recipe =>

          val dslRecipe = com.ing.baker.recipe.javadsl.Recipe(recipe.name)

          val compiledRecipe = RecipeCompiler.compileRecipe(dslRecipe)

          complete("compilation errors: " + compiledRecipe.validationErrors.mkString(","))
        }
      }
    } ~ path("recipe") {
      post {
        entity(as[Recipe]) { recipe =>

          val dslRecipe = com.ing.baker.recipe.javadsl.Recipe(recipe.name)

          val compiledRecipe = RecipeCompiler.compileRecipe(dslRecipe)

          try {
            println(s"Adding recipe called: ${compiledRecipe.name}")
            val recipeId = baker.addRecipe(compiledRecipe)
            complete(recipeId)
          } catch {
            case e: Exception => {
              println(s"Exception when adding recipe: ${e.getMessage}")
              throw e
            }
          }
        }
      }
    }

      testRoute ~
        pathPrefix("recipe") { recipeRoutes() } ~
        pathPrefix("process" / Segment) { processRoutes _ }

  }
}