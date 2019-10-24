package com.erkan

import scala.concurrent.Await
import scala.concurrent.duration._

import com.erkan.Commands._

import akka.actor.Actor
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout

class QueueServiceActor(proxyActor: ActorRef) extends Actor {

  override def receive: Receive = {

    case DelayServiceCommands.QueueCreateCommand(username: String, password: String, prefix: String) => {

      val queueComPrefix = prefix
      val queueNamePrefix = queueComPrefix + "-level-"

      for (levelNumber <- 0 to 27) {

        var queueName = ""
        val expration = math.pow(2, levelNumber) * 1000
        var deadLetterExchange = ""

        if (levelNumber == 0) {
          queueName = queueNamePrefix + "0" + levelNumber.toString()
          deadLetterExchange = queueComPrefix + "-delivery"
        } else {

          if (levelNumber < 10) {
            queueName = queueNamePrefix + "0" + levelNumber.toString()
            deadLetterExchange = queueNamePrefix + "0" + (levelNumber - 1).toString()
          } else if (levelNumber == 10) {

            queueName = queueNamePrefix + levelNumber.toString()
            deadLetterExchange = queueNamePrefix + "0" + (levelNumber - 1).toString()
          } else {

            queueName = queueNamePrefix + levelNumber.toString()
            deadLetterExchange = queueNamePrefix + (levelNumber - 1).toString()
          }

        }

        println(f"queueName: $queueName%s => expration $expration%2.0f => $deadLetterExchange%s")
        
        sender ! OperationResult(true)

      }

    }

    case DelayServiceCommands.QueueClearCommand(username: String, password: String, prefix: String) => {

      val sender = this.sender()
      implicit val timeout = Timeout(30 seconds)
      val future = proxyActor ? RabbitMqProxyCommands.QueueListRabbitMqCommand(username, password)

      val actorResult = Await.result(future, timeout.duration).asInstanceOf[QueueListResult]
      println(actorResult.result)

      for (queueItem <- actorResult.result) {

        if (queueItem.indexOf(prefix) != -1) {

          println("Silinecek query:" + queueItem)

          val deleteFuture = proxyActor ? RabbitMqProxyCommands.QueueDeleteRabbitMqCommand(username, password, queueItem)

          val deleteResult = Await.result(deleteFuture, timeout.duration).asInstanceOf[OperationResult]

          println("Silinecek sonucu:" + deleteResult.isSuccess)

          if (!deleteResult.isSuccess) {

            sender ! OperationResult(false)

          }

        }
      }

      sender ! OperationResult(true)
    }

    case DelayServiceCommands.ExchangeCreateCommand(username: String, password: String, prefix: String) => {

      val queueComPrefix = prefix
      val queueNamePrefix = queueComPrefix + "-level-"

      for (levelNumber <- 0 to 27) {

        var exchangeName = ""

        if (levelNumber == 0) {

          exchangeName = queueNamePrefix + "deliver"

        } else {

          if (levelNumber < 10) {

            exchangeName = queueNamePrefix + "0" + levelNumber.toString()

          } else {

            exchangeName = queueNamePrefix + levelNumber.toString()

          }
        }

        println("yaratılan exchange :" + exchangeName)
        
        sender ! OperationResult(true)

      }

    }

    case _ => { println("I don't know what are you talking about") }

  }
}





