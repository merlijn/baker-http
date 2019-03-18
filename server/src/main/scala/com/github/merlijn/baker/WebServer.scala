package com.github.merlijn.baker

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import akka.stream.ActorMaterializer
import com.github.merlijn.baker.api.BakerRoutes
import com.ing.baker.runtime.core.Baker
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object WebServer extends App {

    val log = LoggerFactory.getLogger("com.github.merlijn.baker.WebServer")

    val configFile = System.getenv().getOrDefault("APPLICATION_CONF", "local.conf")

    log.warn("Using configuration file: " + configFile)

    val config = ConfigFactory.load(configFile)

    implicit val system = ActorSystem("baker", config)

    implicit val materializer = ActorMaterializer()

    val kubernetes = false

    val baker = Baker(system)
    val interface = config.getString("http.interface")
    val port = config.getInt("http.port")

    val routes = BakerRoutes(baker)

    // this bootstraps the cluster
    config.getString("akka.actor.provider") match {
        case "cluster" | "akka.cluster.ClusterActorRefProvider" =>
            log.info("Starting akka management")
            AkkaManagement(system).start()
            if (config.getList("akka.cluster.seed-nodes").isEmpty) {
                log.info("Bootstrapping the akka cluster")
                ClusterBootstrap(system).start()
            }
        case _ =>
    }

    Http(system).bindAndHandle(routes, interface, port)

    println(s"Server online at http://$interface:$port")
}

