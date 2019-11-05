package com.erkan

case class OperationResult(val isSuccess: Boolean)

case class QueueItem(name: String)

case class QueueListResult(result: List[String])

case class ExchangeItem(name: String)

case class ExchangeListResult(result: List[String])

object LevelName {

  def apply(prefix: String, level: Int): String =
    {

      if (level == -1) {

        return prefix + "-" + "delivery"

      }

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

      var routingText: String = ""

      for (levelNumber <- 0 until 27 - level) {

        routingText += "*."

      }

      if (isExchange) {

        routingText += "0"

      } else {

        routingText += "1"

      }

      routingText += ".#"

      return routingText

    }

}

class DelayedMessage {

  private var _exchange: String = ""
  private var _routeKey: String = ""

  def this(prefix: String, expire: Int, destination: String) {

    this()

    _exchange = prefix + "-level-27"

    val binaryArray = expire.toBinaryString.split("")
     
    var combinatedArray: Array[String] = binaryArray

    for (x <- 0 to 27 - binaryArray.length) {

      combinatedArray = "0" +: combinatedArray
    }

    combinatedArray = combinatedArray :+ destination

    _routeKey = combinatedArray.mkString(".")

  }

  def exchange: String = _exchange
  def routeKey: String = _routeKey

}