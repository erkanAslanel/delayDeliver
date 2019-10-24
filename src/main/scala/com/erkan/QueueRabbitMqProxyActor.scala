package com.erkan
 
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.erkan.Commands.RabbitMqProxyCommands
import akka.actor.Actor
import play.api.libs.json.JsResult
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.libs.ws.WSAuthScheme
import play.api.libs.ws.ahc.StandaloneAhcWSClient

class QueueRabbitMqProxyActor(val wsClient: StandaloneAhcWSClient) extends Actor {

  override def receive: Receive = {

    case RabbitMqProxyCommands.QueuePostRabbitMqCommand(username: String, password: String, queueName: String, exprire: Double, deadLettterExchange: String) => {

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

      }

    }

    case RabbitMqProxyCommands.ExchangePostRabbitMqCommand(username: String, password: String, exchangeName: String) => {

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

      }
    }
    
    case RabbitMqProxyCommands.BindingToExchangeToQueuePostRabbitMqCommand(username: String, password: String,exchangeName : String,queueName : String,routing : String)  => {
      
    
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

      }
      
    }
    
      case RabbitMqProxyCommands.BindingToExchangeToExchangePostRabbitMqCommand(username: String, password: String,exchangeName : String,toExchangeName : String,routing : String)  => {
      
    
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

      }
      
    }
    
      case RabbitMqProxyCommands.QueueDeleteRabbitMqCommand(username: String, password: String,queueName : String)  => {
      
    
       val sender = this.sender()    
            

      wsClient.url("http://10.0.0.157:15672/api/queues/%2f/"+queueName).withAuth(username, password, WSAuthScheme.BASIC).delete().map { response =>

        val status: Int = response.status

        if (status == 204) {

          sender ! OperationResult(true)

        } else {

          sender ! OperationResult(false)

        }

      } 
      
    }
    
       case RabbitMqProxyCommands.QueueListRabbitMqCommand(username: String, password: String)  => {
      
    
       val sender = this.sender() 
        
       implicit val reads = Json.reads[QueueItem] 
    
       val futureResult: Future[JsResult[List[QueueItem]]] = wsClient.url("http://10.0.0.157:15672/api/queues/%2f/")
       .withAuth(username, password, WSAuthScheme.BASIC).get().map
       {
         response =>  (
          
             Json.parse(response.body)).validate[List[QueueItem]] 
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