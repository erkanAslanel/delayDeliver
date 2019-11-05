package com.erkan.Commands

object DelayServiceCommands {
  
  case class QueueCreateCommand(username: String, password: String,prefix:String)
  
  case class QueueClearCommand(username: String, password: String,prefix:String)
  
  case class ExchangeCreateCommand(username: String, password: String,prefix:String)
  
   case class ExchangeClearCommand(username: String, password: String,prefix:String)
   
   case class SetBindingCommand(username: String, password: String,prefix:String,level:Int)
   
}