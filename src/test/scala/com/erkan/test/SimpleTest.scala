package com.erkan.test

import com.erkan._
import akka.actor._
import akka.stream.ActorMaterializer
import akka.testkit.{ ImplicitSender, TestKit, TestProbe }
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }
import scala.concurrent.duration._
import play.api.libs.ws._
import play.api.libs.ws.ahc.StandaloneAhcWSClient

class SimpleTest extends TestKit(ActorSystem("TestSpec")) with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 30 seconds)
  }

  "Queue actor" must {

    "create queue" in {

      val actor = system.actorOf(Props(classOf[QueueCreator]), "queueCreator")

      actor ! QueueCommands.QueueCreateCommand("docker", "41594159", "nsb.delay")

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

    val actor2 = system.actorOf(Props(classOf[QueueRabbitMqProxy], StandaloneAhcWSClient()), "queueRabbitMqProxy")

    actor2 ! QueueCommands.QueuePostRabbitMqCommand("docker", "41594159", "test5", math.pow(2, 27) * 1000, "test")

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

    val actor2 = system.actorOf(Props(classOf[QueueRabbitMqProxy], StandaloneAhcWSClient()), "queueRabbitMqProxy")

    actor2 ! QueueCommands.ExchangePostRabbitMqCommand("docker", "41594159", "test6")

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

    val actor2 = system.actorOf(Props(classOf[QueueRabbitMqProxy], StandaloneAhcWSClient()), "queueRabbitMqProxy")

    actor2 ! QueueCommands.BindingToExchangeToQueuePostRabbitMqCommand("docker", "41594159", "test5", "test5", "test-route")

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

    val actor2 = system.actorOf(Props(classOf[QueueRabbitMqProxy], StandaloneAhcWSClient()), "queueRabbitMqProxy")

    actor2 ! QueueCommands.BindingToExchangeToExchangePostRabbitMqCommand("docker", "41594159", "test5", "test6", "test-route2")

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

    val actor2 = system.actorOf(Props(classOf[QueueRabbitMqProxy], StandaloneAhcWSClient()), "queueRabbitMqProxy")

    actor2 ! QueueCommands.QueueDeleteRabbitMqCommand("docker", "41594159", "test5")

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

    val actor2 = system.actorOf(Props(classOf[QueueRabbitMqProxy], StandaloneAhcWSClient()), "queueRabbitMqProxy")

    val probe = TestProbe()

    actor2 ! QueueCommands.QueueListRabbitMqCommand("docker", "41594159")

    val actionResult = expectMsgPF(10 seconds) {

      case QueueListResult(result: List[String]) => {

        println(result)

        "ok"

      }

    }

    actionResult should equal("ok")

  }

}

class SimpleTest8 extends TestKit(ActorSystem("TestSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 30 seconds)
  }

  "Queue actor" must {

    "create queue" in {

      implicit val materializer: ActorMaterializer = ActorMaterializer()
      implicit val executionContext = materializer.executionContext

      val proxyActor = system.actorOf(Props(classOf[QueueRabbitMqProxy], StandaloneAhcWSClient()), "proxyActor")

      val actor = system.actorOf(Props(classOf[QueueCreator], proxyActor), "queueCreator")

      actor ! QueueCommands.QueueClearCommand("docker", "41594159", "nsb.delay")

    }

  }

}


