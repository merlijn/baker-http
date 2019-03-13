package com.github.merlijn.baker.api

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{Directives, Route}
import com.ing.baker.compiler.RecipeCompiler
import com.ing.baker.recipe.javadsl
import com.ing.baker.runtime.core.{Baker, ProcessEvent}
import com.ing.baker.types.Value

import scala.concurrent.duration._
import DSLJsonCodecs._
import EntityMarshalling._

object BakerRoutes extends Directives {

  implicit val timeout: FiniteDuration = 30 seconds

  val defaultEventConfirm = "received"

  val catalogue = new Catalogue()

  def apply(baker: Baker)(implicit actorSystem: ActorSystem): Route = {

    val scalaJSRoutes = {
      pathSingleSlash {
        get {
          complete {
            Html.index
          }
        }
      } ~
        pathPrefix("js" / Remaining) { file =>
          encodeResponse { getFromResource("js/" + file) }
        } ~ pathPrefix("resources" / Remaining) { file =>
          encodeResponse { getFromResource(file) }
        }
    }

    val catalogueRoutes = {
      pathPrefix("catalogue") {
        path("recipes") {
          get {
            complete(catalogue.allRecipes())
          }
        } ~
        path("interactions") {
          get {
            complete(catalogue.allInteractions())
          }
        }
      }
    }

    def processRoutes(): Route = {

        path("create" / Segment) { recipeId =>
          post {
            val processState = baker.createProcess(recipeId, java.util.UUID.randomUUID().toString)
            complete(processState.processId)
          }
        }  ~ pathPrefix("by_id" / Segment) { processId =>

          path("fire_event") {
            post {
              (entity(as[ProcessEvent]) & parameter('confirm.as[String] ?)) { (event, confirm) =>

                val sensoryEventStatus = confirm.getOrElse(defaultEventConfirm) match {
                  case "received"  => baker.fireEventAsync(processId, event).confirmReceived()
                  case "completed" => baker.fireEventAsync(processId, event).confirmCompleted()
                  case other => throw new IllegalArgumentException(s"Unsupported confirm type: $other")
                }

                complete(sensoryEventStatus)
              }
            }
          } ~ path("events") {
            get {
              complete(baker.getEvents(processId).toList)
            }
          } ~
            path("ingredients") {
              get {

                val ingredients: Map[String, Value] = baker.getIngredients(processId)

                complete(ingredients)
              }
            } ~
            path("visual_state") {
              get {
                complete(baker.getVisualState(processId))
              }
            }
        }
    }

    def recipeRoutes(): Route =
      path("compile") {
        post {
          entity(as[javadsl.Recipe]) { recipe =>

            val compiledRecipe = RecipeCompiler.compileRecipe(recipe)

            import guru.nidi.graphviz.engine.Graphviz
            import guru.nidi.graphviz.parse.Parser

            val graph = Parser.read(compiledRecipe.getRecipeVisualization)

            complete(Graphviz.fromGraph(graph))
          }
        }
      } ~ path("add") {
        post {
          entity(as[javadsl.Recipe]) { recipe =>

            val compiledRecipe = RecipeCompiler.compileRecipe(recipe)

            try {
              val recipeId = baker.addRecipe(compiledRecipe, allowMissingImplementations = true)
              complete(recipeId)
            } catch {
              case e: Exception => {
                println(s"Exception when adding recipe: ${e.getMessage}")
                throw e
              }
            }
          }
        }
      } ~ path("svg" / Segment) { recipeId =>

        get {

          import guru.nidi.graphviz.engine.Graphviz
          import guru.nidi.graphviz.parse.Parser

          val recipe = baker.getRecipe(recipeId)
          val graph = Parser.read(recipe.getRecipeVisualization)

          complete(Graphviz.fromGraph(graph))
        }
      }

      scalaJSRoutes ~
        catalogueRoutes ~
        pathPrefix("recipe") { recipeRoutes() } ~
        pathPrefix("process") { processRoutes() }

  }
}