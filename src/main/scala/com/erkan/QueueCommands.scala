package com.erkan

object QueueCommands {

  case class QueueCreateCommand()

  case class QueuePostRabbitMqCommand(username: String, password: String, queueName: String, exprire: String)

}