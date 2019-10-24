package com.erkan

 

object QueueCommands {

  case class QueueCreateCommand(username: String, password: String,prefix:String)
  
  case class QueueClearCommand(username: String, password: String,prefix:String)

  case class QueuePostRabbitMqCommand(username: String, password: String, queueName: String, exprire: Double,deadLettterExchange:String)
  
  case class ExchangePostRabbitMqCommand(username: String, password: String,exchangeName : String)
  
  case class BindingToExchangeToQueuePostRabbitMqCommand(username: String, password: String,exchangeName : String,queueName : String,routing:String)
   
  case class BindingToExchangeToExchangePostRabbitMqCommand(username: String, password: String,exchangeName : String,toExchangeName : String,routing:String)
  
  case class QueueDeleteRabbitMqCommand( username: String, password: String,queueName : String)
  
  case class QueueListRabbitMqCommand(username: String, password: String)
}