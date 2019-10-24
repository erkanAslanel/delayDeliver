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

class SimpleTest extends TestKit(ActorSystem("TestSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 30 seconds)
  }

  "Queue actor" must {

    "create queue" in {

      implicit val materializer: ActorMaterializer = ActorMaterializer()
      implicit val executionContext = materializer.executionContext

      val proxyActor = system.actorOf(Props(classOf[QueueRabbitMqProxyActor], StandaloneAhcWSClient()), "proxyActor")

      val actor = system.actorOf(Props(classOf[QueueServiceActor], proxyActor), "service")

      actor ! DelayServiceCommands.QueueCreateCommand("docker", "41594159", "nsb.delay")

      expectMsg(OperationResult(true))

    }

  }

}

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
    TestKit.shutdownActorSystem(system, duration = 30 seconds)
  }

  "Queue actor" must {

    "create queue" in {

      implicit val materializer: ActorMaterializer = ActorMaterializer()
      implicit val executionContext = materializer.executionContext

      val sender = TestProbe()

      val proxyActor = system.actorOf(Props(classOf[QueueRabbitMqProxyActor], StandaloneAhcWSClient()), "proxyActor")

      val child = sender.childActorOf(Props(classOf[QueueServiceActor], proxyActor), "queueCreator")

      sender.send(child, DelayServiceCommands.QueueClearCommand("docker", "41594159", "nsb.delay"))

      sender.expectMsg(30 seconds, OperationResult(true))

    }

  }

}

class SimpleTest9 extends TestKit(ActorSystem("TestSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 30 seconds)
  }

  "create rabbitmq proxy" in {

    implicit val materializer: ActorMaterializer = ActorMaterializer()

    implicit val executionContext = materializer.executionContext

    val proxyActor = system.actorOf(Props(classOf[QueueRabbitMqProxyActor], StandaloneAhcWSClient()), "proxyActor")

    val actor2 = system.actorOf(Props(classOf[QueueServiceActor], proxyActor), "service")

    val probe = TestProbe()

    actor2 ! DelayServiceCommands.ExchangeCreateCommand("docker", "41594159", "nsb.delay")

    expectMsg(OperationResult(true))

  }

}


