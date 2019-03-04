package com.github.merlijn.baker

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.github.merlijn.baker.api.BakerRoutes
import com.ing.baker.runtime.core.Baker

object WebServer {
  def main(args: Array[String]) {
    implicit val system = ActorSystem("server-system")
    implicit val materializer = ActorMaterializer()

    val config = system.settings.config

    val baker = new Baker()(system)
    val interface = config.getString("http.interface")
    val port = config.getInt("http.port")

    val routes = BakerRoutes(baker)

    Http().bindAndHandle(routes, interface, port)

    println(s"Server online at http://$interface:$port")
  }
}
