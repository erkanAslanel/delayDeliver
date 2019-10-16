package com.erkan

//#quick-start-server
import scala.concurrent.ExecutionContext

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.actor.Props
import akka.actor.ActorSystem

//#main-class
object QuickstartServer extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("rabbitmqDelaySystemGenerator")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  val actor = actorSystem.actorOf(Props(classOf[QueueCreator],materializer), "summingactor")

  actor ! QueueCommands.QueueCreateCommand
  


}

//#main-class
//#quick-start-server
