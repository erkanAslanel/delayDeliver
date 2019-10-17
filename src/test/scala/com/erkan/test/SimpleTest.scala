package com.erkan.test

import com.erkan._
import akka.actor._
import akka.stream.ActorMaterializer
import akka.testkit.{ ImplicitSender, TestKit }
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

      actor ! QueueCommands.QueueCreateCommand

    }

  }

}

class SimpleTest2 extends TestKit(ActorSystem("rabbitmqDelaySystemGenerator")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {

    super.afterAll()
    TestKit.shutdownActorSystem(system, duration = 30 seconds)
  }

  "create rabbitmq proxy" in {

    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext = materializer.executionContext

    val actor2 = system.actorOf(Props(classOf[QueueRabbitMqProxy], StandaloneAhcWSClient()), "queueRabbitMqProxy")

    actor2 ! QueueCommands.QueuePostRabbitMqCommand("test", "test2", "test3", "test4")

    expectMsg(OperationResult(true))

  }

}


