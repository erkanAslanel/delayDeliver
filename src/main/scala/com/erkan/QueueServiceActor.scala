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

        implicit val timeout = Timeout(30 seconds)

        val createFuture = proxyActor ? RabbitMqProxyCommands.QueuePostRabbitMqCommand(username, password, queueName, expration, deadLetterExchange)

        val createResult = Await.result(createFuture, timeout.duration).asInstanceOf[OperationResult]

        if (!createResult.isSuccess) {

          sender ! OperationResult(false)
          return null
        }

      }

      sender ! OperationResult(true)

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

      for (levelNumber <- -1 to 27) {

        var exchangeName = ""

        if (levelNumber == -1) {

          exchangeName = queueComPrefix + "-delivery"

        } else {

          if (levelNumber < 10) {

            exchangeName = queueNamePrefix + "0" + levelNumber.toString()

          } else {

            exchangeName = queueNamePrefix + levelNumber.toString()

          }
        }

        implicit val timeout = Timeout(30 seconds)

        val createFuture = proxyActor ? RabbitMqProxyCommands.ExchangePostRabbitMqCommand(username, password, exchangeName)

        val createResult = Await.result(createFuture, timeout.duration).asInstanceOf[OperationResult]

        if (!createResult.isSuccess) {

          sender ! OperationResult(false)
          return null
        }

      }

      sender ! OperationResult(true)

    }

    case DelayServiceCommands.ExchangeClearCommand(username: String, password: String, prefix: String) => {

      val sender = this.sender()
      implicit val timeout = Timeout(30 seconds)
      val future = proxyActor ? RabbitMqProxyCommands.ExchangeListRabbitmqCommand(username, password)

      val actorResult = Await.result(future, timeout.duration).asInstanceOf[ExchangeListResult]
      println(actorResult.result)

      for (exchangeItem <- actorResult.result) {

        if (exchangeItem.indexOf(prefix) != -1) {

          println("Silinecek exchange:" + exchangeItem)

          val deleteFuture = proxyActor ? RabbitMqProxyCommands.ExchangeDeleteRabbitMqCommand(username, password, exchangeItem)

          val deleteResult = Await.result(deleteFuture, timeout.duration).asInstanceOf[OperationResult]

          println("Silinecek sonucu:" + deleteResult.isSuccess)

          if (!deleteResult.isSuccess) {

            sender ! OperationResult(false)

          }

        }
      }

      sender ! OperationResult(true)

    }

    case DelayServiceCommands.SetBindingCommand(username: String, password: String, prefix: String, level: Int) => {

      val sender = this.sender()

      val baseExchange = LevelName(prefix, level)
      val queueName = LevelName(prefix, level)
      val toExchange = LevelName(prefix, level - 1)

      val routingQueueName = RoutingCode(level, false)

      val routingExchange = RoutingCode(level, true)

      implicit val timeout = Timeout(30 seconds)

      val toQueueFuture = proxyActor ? RabbitMqProxyCommands.BindingToExchangeToQueuePostRabbitMqCommand(username, password, baseExchange, queueName, routingQueueName)

      val toQueueResult = Await.result(toQueueFuture, timeout.duration).asInstanceOf[OperationResult]

      if (toQueueResult.isSuccess == false) {

        sender ! OperationResult(false)
        return null
      }

      val toExchangeFuture = proxyActor ? RabbitMqProxyCommands.BindingToExchangeToExchangePostRabbitMqCommand(username, password, baseExchange, toExchange, routingExchange)
      val toExchangeResult = Await.result(toExchangeFuture, timeout.duration).asInstanceOf[OperationResult]

      sender ! toExchangeResult
    }

    case DelayServiceCommands.SetBindingAllCommand(username: String, password: String, prefix: String) => {

      val sender = this.sender()

      for (level <- 0 to 27) {

        val baseExchange = LevelName(prefix, level)
        val queueName = LevelName(prefix, level)
        val toExchange = LevelName(prefix, level - 1)

        val routingQueueName = RoutingCode(level, false)

        val routingExchange = RoutingCode(level, true)

        implicit val timeout = Timeout(30 seconds)

        val toQueueFuture = proxyActor ? RabbitMqProxyCommands.BindingToExchangeToQueuePostRabbitMqCommand(username, password, baseExchange, queueName, routingQueueName)

        val toQueueResult = Await.result(toQueueFuture, timeout.duration).asInstanceOf[OperationResult]

        if (toQueueResult.isSuccess == false) {

          sender ! OperationResult(false)
          return null
        }

        val toExchangeFuture = proxyActor ? RabbitMqProxyCommands.BindingToExchangeToExchangePostRabbitMqCommand(username, password, baseExchange, toExchange, routingExchange)
        val toExchangeResult = Await.result(toExchangeFuture, timeout.duration).asInstanceOf[OperationResult]

        if (toExchangeResult.isSuccess == false) {

          sender ! OperationResult(false)
          return null
        }

      }
      
      sender ! OperationResult(true)

    }
    
     case DelayServiceCommands.SendDelayedMessage(username: String, password: String,prefix:String,endQueue:String,body:String,expire:Int) => {

      val sender = this.sender()
      
      val delayedMessage = new DelayedMessage(prefix,expire,endQueue)

       implicit val timeout = Timeout(30 seconds)
       
       val exchangeName = LevelName(prefix,27)

        val toQueueFuture = proxyActor ? RabbitMqProxyCommands.ExchangePublishMessage(username, password,delayedMessage.exchange,delayedMessage.routeKey,body)

        val toQueueResult = Await.result(toQueueFuture, timeout.duration).asInstanceOf[OperationResult]

        if (toQueueResult.isSuccess == false) {

          sender ! OperationResult(false)
          return null
        }
       
      
      sender ! OperationResult(true)

    }
     

    case _ => { println("I don't know what are you talking about") }

  }
}





