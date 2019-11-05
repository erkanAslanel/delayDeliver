package com.erkan.test

import com.erkan._
import com.erkan.Commands._
import akka.actor._
import akka.stream.ActorMaterializer
import akka.testkit.{ ImplicitSender, TestKit, TestProbe }
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }
import scala.concurrent.duration._
import play.api.libs.ws._
import play.api.libs.ws.ahc.StandaloneAhcWSClient

class SimpleTest2 extends TestKit(ActorSystem("TestSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 30 seconds)
  }

  "create rabbitmq proxy" in {

    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext = materializer.executionContext

    val actor2 = system.actorOf(Props(classOf[QueueServiceActor], StandaloneAhcWSClient()), "queueRabbitMqProxy")

    actor2 ! RabbitMqProxyCommands.QueuePostRabbitMqCommand("docker", "41594159", "test5", math.pow(2, 27) * 1000, "test")

    expectMsg(10 seconds, OperationResult(true))

  }

}

class SimpleTest3 extends TestKit(ActorSystem("TestSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 30 seconds)
  }

  "create rabbitmq proxy" in {

    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext = materializer.executionContext

    val actor2 = system.actorOf(Props(classOf[QueueServiceActor], StandaloneAhcWSClient()), "queueRabbitMqProxy")

    actor2 ! RabbitMqProxyCommands.ExchangePostRabbitMqCommand("docker", "41594159", "test6")

    expectMsg(10 seconds, OperationResult(true))

  }

}

class SimpleTest4 extends TestKit(ActorSystem("TestSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 30 seconds)
  }

  "create rabbitmq proxy" in {

    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext = materializer.executionContext

    val actor2 = system.actorOf(Props(classOf[QueueServiceActor], StandaloneAhcWSClient()), "queueRabbitMqProxy")

    actor2 ! RabbitMqProxyCommands.BindingToExchangeToQueuePostRabbitMqCommand("docker", "41594159", "test5", "test5", "test-route")

    expectMsg(10 seconds, OperationResult(true))

  }

}

class SimpleTest5 extends TestKit(ActorSystem("TestSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 30 seconds)
  }

  "create rabbitmq proxy" in {

    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext = materializer.executionContext

    val actor2 = system.actorOf(Props(classOf[QueueServiceActor], StandaloneAhcWSClient()), "queueRabbitMqProxy")

    actor2 ! RabbitMqProxyCommands.BindingToExchangeToExchangePostRabbitMqCommand("docker", "41594159", "test5", "test6", "test-route2")

    expectMsg(10 seconds, OperationResult(true))

  }

}

class SimpleTest6 extends TestKit(ActorSystem("TestSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 30 seconds)
  }

  "create rabbitmq proxy" in {

    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext = materializer.executionContext

    val actor2 = system.actorOf(Props(classOf[QueueServiceActor], StandaloneAhcWSClient()), "queueRabbitMqProxy")

    actor2 ! RabbitMqProxyCommands.QueueDeleteRabbitMqCommand("docker", "41594159", "test5")

    expectMsg(10 seconds, OperationResult(true))

  }

}

class SimpleTest7 extends TestKit(ActorSystem("TestSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 30 seconds)
  }

  "create rabbitmq proxy" in {

    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext = materializer.executionContext

    val actor2 = system.actorOf(Props(classOf[QueueServiceActor], StandaloneAhcWSClient()), "queueRabbitMqProxy")

    val probe = TestProbe()

    actor2 ! RabbitMqProxyCommands.QueueListRabbitMqCommand("docker", "41594159")

    val actionResult = expectMsgPF(10 seconds) {

      case QueueListResult(result: List[String]) => {

        println(result)

        "ok"

      }

    }

    actionResult should equal("ok")

  }

}

class SimpleTest8 extends TestKit(ActorSystem("TestSpec")) with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 2 minute)
  }

  "Queue actor" must {

    "create queue" in {

      implicit val materializer: ActorMaterializer = ActorMaterializer()
      implicit val executionContext = materializer.executionContext

      val sender = TestProbe()

      val proxyActor = system.actorOf(Props(classOf[QueueRabbitMqProxyActor], StandaloneAhcWSClient()), "proxyActor")

      val child = sender.childActorOf(Props(classOf[QueueServiceActor], proxyActor), "queueCreator")

      sender.send(child, DelayServiceCommands.QueueClearCommand("docker", "41594159", "nsb.delay"))

      sender.expectMsg(2 minute, OperationResult(true))

    }

  }

}

class SimpleTest10 extends TestKit(ActorSystem("TestSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 30 seconds)
  }

  "create rabbitmq proxy" in {

    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext = materializer.executionContext

    val proxyActor = system.actorOf(Props(classOf[QueueRabbitMqProxyActor], StandaloneAhcWSClient()), "proxyActor")

    val probe = TestProbe()

    proxyActor ! RabbitMqProxyCommands.ExchangeListRabbitmqCommand("docker", "41594159")

    val actionResult = expectMsgPF(10 seconds) {

      case ExchangeListResult(result: List[String]) => {

        println(result)

        "ok"

      }

    }

    actionResult should equal("ok")

  }

}

class SimpleTest11 extends TestKit(ActorSystem("TestSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 30 seconds)
  }

  "create rabbitmq proxy" in {

    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext = materializer.executionContext

    val proxyActor = system.actorOf(Props(classOf[QueueRabbitMqProxyActor], StandaloneAhcWSClient()), "proxyActor")

    val probe = TestProbe()

    proxyActor ! RabbitMqProxyCommands.ExchangeDeleteRabbitMqCommand("docker", "41594159", "test6")

    expectMsg(OperationResult(true))

  }

}

class SimpleTest13 extends TestKit(ActorSystem("TestSpec")) with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 30 seconds)
  }

  "Queue actor" must {

    "create queue" in {

      implicit val materializer: ActorMaterializer = ActorMaterializer()
      implicit val executionContext = materializer.executionContext

      val sender = TestProbe()

      val proxyActor = system.actorOf(Props(classOf[QueueRabbitMqProxyActor], StandaloneAhcWSClient()), "proxyActor")

      val child = sender.childActorOf(Props(classOf[QueueServiceActor], proxyActor), "queueCreator")

      sender.send(child, DelayServiceCommands.SetBindingCommand("docker", "41594159", "nsb.delay", 0))

      sender.expectMsg(30 seconds, OperationResult(true))

    }

  }

}

class SimpleTest14 extends TestKit(ActorSystem("TestSpec")) with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 30 seconds)
  }

  "Queue actor" must {

    "level-1" in {

      val route = RoutingCode(1, true)

      println(route)

      assert(route == "*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.0.#")

    }

    "level-1-queue" in {

      val route = RoutingCode(1, false)

      println(route)

      assert(route == "*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.1.#")

    }

    "level-27" in {

      val route = RoutingCode(27, true)

      println(route)

      assert(route == "0.#")

    }

    "exchangeName-level-0" in {

      val exchangeName = LevelName("nsb.delay", -1)

      println(exchangeName)

      assert(exchangeName == "nsb.delay-delivery")

    }

  }

}

class SimpleTest15 extends TestKit(ActorSystem("TestSpec")) with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 30 seconds)
  }

  "Queue actor" must {

    "create queue" in {

      implicit val materializer: ActorMaterializer = ActorMaterializer()
      implicit val executionContext = materializer.executionContext

      val sender = TestProbe()

      val proxyActor = system.actorOf(Props(classOf[QueueRabbitMqProxyActor], StandaloneAhcWSClient()), "proxyActor")

      val child = sender.childActorOf(Props(classOf[QueueServiceActor], proxyActor), "queueCreator")

      sender.send(child, DelayServiceCommands.ExchangeClearCommand("docker", "41594159", "nsb.delay"))

      sender.expectMsg(30 seconds, OperationResult(true))

    }

  }

}

class SimpleTest16 extends TestKit(ActorSystem("TestSpec")) with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 3 minute)
  }

  "Queue actor" must {

    "create queue" in {

      implicit val materializer: ActorMaterializer = ActorMaterializer()
      implicit val executionContext = materializer.executionContext

      val sender = TestProbe()

      val proxyActor = system.actorOf(Props(classOf[QueueRabbitMqProxyActor], StandaloneAhcWSClient()), "proxyActor")

      val child = sender.childActorOf(Props(classOf[QueueServiceActor], proxyActor), "queueCreator")

      sender.send(child, DelayServiceCommands.QueueCreateCommand("docker", "41594159", "nsb.delay"))

      sender.expectMsg(2 minute, OperationResult(true))

    }

  }

}

class SimpleTest17 extends TestKit(ActorSystem("TestSpec")) with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 3 minute)
  }

  "Queue actor" must {

    "create queue" in {

      implicit val materializer: ActorMaterializer = ActorMaterializer()
      implicit val executionContext = materializer.executionContext

      val sender = TestProbe()

      val proxyActor = system.actorOf(Props(classOf[QueueRabbitMqProxyActor], StandaloneAhcWSClient()), "proxyActor")

      val child = sender.childActorOf(Props(classOf[QueueServiceActor], proxyActor), "queueCreator")

      sender.send(child, DelayServiceCommands.ExchangeCreateCommand("docker", "41594159", "nsb.delay"))

      sender.expectMsg(2 minute, OperationResult(true))

    }

  }

}

class SimpleTest18 extends TestKit(ActorSystem("TestSpec")) with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 3 minute)
  }

  "Queue actor" must {

    "create queue" in {

      implicit val materializer: ActorMaterializer = ActorMaterializer()
      implicit val executionContext = materializer.executionContext

      val sender = TestProbe()

      val proxyActor = system.actorOf(Props(classOf[QueueRabbitMqProxyActor], StandaloneAhcWSClient()), "proxyActor")

      val child = sender.childActorOf(Props(classOf[QueueServiceActor], proxyActor), "queueCreator")

      sender.send(child, DelayServiceCommands.SetBindingAllCommand("docker", "41594159", "nsb.delay"))

      sender.expectMsg(2 minute, OperationResult(true))

    }

  }

}

class SimpleTest19 extends TestKit(ActorSystem("TestSpec")) with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 30 seconds)
  }

  "Queue actor" must {

    "create queue" in {

      implicit val materializer: ActorMaterializer = ActorMaterializer()
      implicit val executionContext = materializer.executionContext

      val proxyActor = system.actorOf(Props(classOf[QueueRabbitMqProxyActor], StandaloneAhcWSClient()), "proxyActor")

      val probe = TestProbe()

      probe.send(proxyActor, RabbitMqProxyCommands.ExchangePublishMessage("docker", "41594159", "nsb.delay-level-27", "0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.1.0.1.0.Vendor", "test"))

      probe.expectMsg(30 seconds, OperationResult(true))

    }

  }

}

class SimpleTest20 extends TestKit(ActorSystem("TestSpec")) with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 3 minute)
  }

  "Queue actor" must {

    "create queue" in {

      implicit val materializer: ActorMaterializer = ActorMaterializer()
      implicit val executionContext = materializer.executionContext

      val sender = TestProbe()

      val proxyActor = system.actorOf(Props(classOf[QueueRabbitMqProxyActor], StandaloneAhcWSClient()), "proxyActor")

      val child = sender.childActorOf(Props(classOf[QueueServiceActor], proxyActor), "queueCreator")

      sender.send(child, DelayServiceCommands.SendDelayedMessage("docker", "41594159", "nsb.delay", "Transfer", "test2", 150))

      sender.expectMsg(2 minute, OperationResult(true))

    }

  }

}

class SimpleTest21 extends TestKit(ActorSystem("TestSpec")) with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 3 minute)
  }

  "Queue actor" must {

    "create queue" in {

      val delayMessage = new DelayedMessage("nsb.delay", 10, "Vendor")

      println(delayMessage.exchange)
      println(delayMessage.routeKey)

      assert(delayMessage.exchange == "nsb.delay-level-27")
      assert(delayMessage.routeKey == "0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.1.0.1.0.Vendor")

    }

  }

}

