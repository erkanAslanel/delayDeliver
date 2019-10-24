package com.erkan
 
import akka.actor.Actor
import akka.actor.ActorRef
import akka.stream.ActorMaterializer
 
import play.api.libs.ws.ahc.StandaloneAhcWSClient
 

import play.api.libs.ws._
import play.api.libs.json._
import play.api.libs.ws.JsonBodyReadables._
import play.api.libs.ws.JsonBodyWritables._

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.util.Timeout
import akka.pattern.ask

class QueueCreator(proxyActor:ActorRef) extends Actor {

  
  
  override def receive: Receive = {

    case QueueCommands.QueueCreateCommand(username: String, password: String,prefix:String) => {

      
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

      }

    }
    
     case QueueCommands.QueueClearCommand(username: String, password: String,prefix:String) => {

         implicit val timeout = Timeout(5 seconds)
    val future = proxyActor ? QueueCommands.QueueListRabbitMqCommand(username,password)
    
    val actorResult = Await.result(future, timeout.duration).asInstanceOf[QueueListResult]
    println(actorResult.result)
     

      
    }

    case _ => { println("I don't know what are you talking about") }

  }
}

class QueueRabbitMqProxy(val wsClient: StandaloneAhcWSClient) extends Actor {

  override def receive: Receive = {

    case QueueCommands.QueuePostRabbitMqCommand(username: String, password: String, queueName: String, exprire: Double, deadLettterExchange: String) => {

      val sender = this.sender()

      val arg = Json.obj(
        "x-message-ttl" -> exprire,
        "x-dead-letter-exchange" -> deadLettterExchange)

      val data = Json.obj(
        "auto_delete" -> false,
        "durable" -> true,
        "arguments" -> arg)

      println(data)

      wsClient.url("http://10.0.0.157:15672/api/queues/%2f/" + queueName).withAuth(username, password, WSAuthScheme.BASIC).put(data).map { response =>

        val status: Int = response.status

        if (status == 204) {

          sender ! OperationResult(true)

        } else {

          sender ! OperationResult(false)

        }

      }.andThen {

        {
          case _ => {

            wsClient.close()
          }
        }

      }

    }

    case QueueCommands.ExchangePostRabbitMqCommand(username: String, password: String, exchangeName: String) => {

      val sender = this.sender()
      
        val data = Json.obj(
        "type" -> "topic",
        "durable" -> true,
     )
        
     

      wsClient.url("http://10.0.0.157:15672/api/exchanges/%2f/" + exchangeName).withAuth(username, password, WSAuthScheme.BASIC).put(data).map { response =>

        val status: Int = response.status

        if (status == 204) {

          sender ! OperationResult(true)

        } else {

          sender ! OperationResult(false)

        }

      }.andThen {

        {
          case _ => {

            wsClient.close()
          }
        }

      }

    }
    
    case QueueCommands.BindingToExchangeToQueuePostRabbitMqCommand(username: String, password: String,exchangeName : String,queueName : String,routing : String)  => {
      
    
       val sender = this.sender()
      
        val data = Json.obj(
        "routing_key" -> routing,
    
     )
        
     

      wsClient.url("http://10.0.0.157:15672/api/bindings/%2f/" + "e/"+exchangeName+"/q/"+ queueName).withAuth(username, password, WSAuthScheme.BASIC).post(data).map { response =>

        val status: Int = response.status

        if (status == 201) {

          sender ! OperationResult(true)

        } else {

          sender ! OperationResult(false)

        }

      }.andThen {

        {
          case _ => {

            wsClient.close()
          }
        }

      }
      
    }
    
      case QueueCommands.BindingToExchangeToExchangePostRabbitMqCommand(username: String, password: String,exchangeName : String,toExchangeName : String,routing : String)  => {
      
    
       val sender = this.sender()
      
        val data = Json.obj(
        "routing_key" -> routing,
    
     )
        
     

      wsClient.url("http://10.0.0.157:15672/api/bindings/%2f/" + "e/"+exchangeName+"/e/"+ toExchangeName).withAuth(username, password, WSAuthScheme.BASIC).post(data).map { response =>

        val status: Int = response.status

        if (status == 201) {

          sender ! OperationResult(true)

        } else {

          sender ! OperationResult(false)

        }

      }.andThen {

        {
          case _ => {

            wsClient.close()
          }
        }

      }
      
    }
    
      case QueueCommands.QueueDeleteRabbitMqCommand(username: String, password: String,queueName : String)  => {
      
    
       val sender = this.sender()
      
        
     

      wsClient.url("http://10.0.0.157:15672/api/queues/%2f/"+queueName).withAuth(username, password, WSAuthScheme.BASIC).delete().map { response =>

        val status: Int = response.status

        if (status == 204) {

          sender ! OperationResult(true)

        } else {

          sender ! OperationResult(false)

        }

      }.andThen {

        {
          case _ => {

            wsClient.close()
          }
        }

      }
      
    }
    
       case QueueCommands.QueueListRabbitMqCommand(username: String, password: String)  => {
      
    
       val sender = this.sender()
      
        
     implicit val reads = Json.reads[QueueItem]
      import play.api.libs.json._
    
       val futureResult: Future[JsResult[List[QueueItem]]] = wsClient.url("http://10.0.0.157:15672/api/queues/%2f/")
       .withAuth(username, password, WSAuthScheme.BASIC).get().map
       { response =>

 (Json.parse(response.body)).validate[List[QueueItem]]
 
        
  
}
       
      
      futureResult.map {
        
        response => {
          
          response.map {
            
            obj => {
              
              sender ! QueueListResult(obj.map({item => item.name }).toList)
              
            }
            
          }
          
        }
        
      }
        
         
     
      
    }

    case _ => {
      println("I don't know what are you talking about")
    }

  }
}



