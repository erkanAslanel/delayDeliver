package com.erkan

import scala.concurrent.ExecutionContext
import com.erkan.Commands._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.actor.Props
import akka.actor.ActorSystem
import play.api.libs.ws._
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import akka.pattern.ask
import akka.util.Timeout

object QuickstartServer extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("rabbitmqDelaySystemGenerator")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  val proxyActor = actorSystem.actorOf(Props(classOf[QueueRabbitMqProxyActor], StandaloneAhcWSClient()), "proxyActor")

  val actor = actorSystem.actorOf(Props(classOf[QueueServiceActor], proxyActor), "service")

  actor ! DelayServiceCommands.QueueCreateCommand("docker", "41594159", "nsb.delay")

}
 
