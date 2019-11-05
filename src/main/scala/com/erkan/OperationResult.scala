package com.erkan

case class OperationResult(val isSuccess: Boolean)

case class QueueItem(name: String)

case class QueueListResult(result: List[String])

case class ExchangeItem(name: String)

case class ExchangeListResult(result: List[String])

object LevelName {

  def apply(prefix: String, level: Int): String =
    {

      var levelNo = level.toString()

      if (level < 10) {

        levelNo = "0" + levelNo
      }

      return prefix + "-" + "level" + "-" + levelNo

    }

}

object RoutingCode {

  def apply(level: Int, isExchange: Boolean): String =
    {

      return level.toBinaryString

    }

}