package com.erkan

import scala.concurrent.ExecutionContext
import com.erkan.Commands._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.actor.Props
import akka.actor.ActorSystem

object QuickstartServer extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("rabbitmqDelaySystemGenerator")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

   val actor2 = actorSystem.actorOf(Props[com.erkan.QueueRabbitMqProxyActor], "summingactor")

   actor2 ! RabbitMqProxyCommands.QueuePostRabbitMqCommand("test", "test2", "test3", 10, "test")

}
 
