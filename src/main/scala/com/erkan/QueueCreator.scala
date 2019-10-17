package com.erkan
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.Actor
import akka.stream.ActorMaterializer
import play.api.libs.ws.DefaultBodyReadables.readableAsString
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import play.api.libs.ws._
import play.api.libs.json._
import play.api.libs.ws.JsonBodyReadables._
import play.api.libs.ws.JsonBodyWritables._

class QueueCreator() extends Actor {

  override def receive: Receive = {

    case QueueCommands.QueueCreateCommand => {

      println(s"queue create from akka 2")

    }

    case _ => { println("I don't know what are you talking about") }

  }
}

class QueueRabbitMqProxy(val wsClient: StandaloneAhcWSClient) extends Actor {

  override def receive: Receive = {

    case QueueCommands.QueuePostRabbitMqCommand(username: String, password: String, queueName: String, exprire: String) => {

      val data = Json.obj(
        "auto_delete" -> false,
        "durable" -> true,
        "arguments" -> "test")

      wsClient.url("/api/queues/%2f/" + queueName).withAuth(username, password, WSAuthScheme.BASIC).put(data).map { response =>

        val statusText: String = response.statusText
        println(s"Got a response $statusText")

        sender() ! OperationResult(true)

      }.andThen {

        {
          case _ =>

            wsClient.close()

        }

      }

    }

    case _ => {
      println("I don't know what are you talking about")
    }

  }
}



