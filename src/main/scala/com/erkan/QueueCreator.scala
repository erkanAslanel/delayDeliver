package com.erkan
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.Actor
import akka.stream.ActorMaterializer
import play.api.libs.ws.DefaultBodyReadables.readableAsString
import play.api.libs.ws.ahc.StandaloneAhcWSClient

class QueueCreator(implicit val materializer: ActorMaterializer) extends Actor {

  val wsClient = StandaloneAhcWSClient()

  override def receive: Receive = {

    case QueueCommands.QueueCreateCommand => {

      println(s"queue create from akka")

      wsClient.url("http://www.tekhnelogos.com").get().map { response ⇒
        val statusText: String = response.statusText
        val body = response.body[String]
        println(s"Got a response $statusText $body")
      }.andThen { case _ => wsClient.close() }

    }

    case _ => { println("I don't know what are you talking about") }

  }
}

class QueueRabbitMqProxy extends Actor {

  override def receive: Receive = {

    case QueueCommands.QueuePostRabbitMqCommand(username: String, password: String, queueName: String, exprire: String) =>

      println(s"QueuePostRabbitMqCommand recived")

      println(s"username $username")

  }
}



