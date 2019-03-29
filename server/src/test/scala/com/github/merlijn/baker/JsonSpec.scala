package com.github.merlijn.baker

import com.github.merlijn.baker.api.Catalogue
import com.github.merlijn.baker.model.{JsonCodecs, Recipe}
import io.circe.Json.{JArray, JObject}
import io.circe.{Decoder, Json, ParsingFailure}
import org.scalatest.{Matchers, WordSpec}

class JsonSpec extends WordSpec with Matchers {

  "The json parser" should {

    "resolve json pointers" in {

      val is = classOf[Catalogue].getResourceAsStream("/catalogue/recipes/webshop.json")

      val json = scala.io.Source.fromInputStream(is).mkString

      val result: Either[ParsingFailure, Json] = io.circe.parser.parse(json)

      val ast = result.right.get

      import JsonCodecs._

      val recipe = JsonCodecs.decodeUnsafe[Recipe](json)

      println(recipe)

    }
  }

}
