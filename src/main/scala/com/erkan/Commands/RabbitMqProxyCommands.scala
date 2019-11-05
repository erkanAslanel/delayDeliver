package com.erkan.Commands
 

object RabbitMqProxyCommands {

  case class QueuePostRabbitMqCommand(username: String, password: String, queueName: String, exprire: Double,deadLettterExchange:String)
  
  case class ExchangePostRabbitMqCommand(username: String, password: String,exchangeName : String)
  
  case class BindingToExchangeToQueuePostRabbitMqCommand(username: String, password: String,exchangeName : String,queueName : String,routing:String)
   
  case class BindingToExchangeToExchangePostRabbitMqCommand(username: String, password: String,exchangeName : String,toExchangeName : String,routing:String)
  
  case class QueueDeleteRabbitMqCommand( username: String, password: String,queueName : String)
  
  case class QueueListRabbitMqCommand(username: String, password: String)
  
  case class ExchangeDeleteRabbitMqCommand(username: String, password: String,queueName : String)
  
  case class ExchangeListRabbitmqCommand(username: String, password: String)
  
  case class ExchangePublishMessage(username: String, password: String,exchangeName: String,routingKey:String,payload:String)
  
}