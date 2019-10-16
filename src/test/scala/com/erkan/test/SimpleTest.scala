package com.erkan.test

import com.erkan.{ QueueCreator, QueueCommands }
import akka.actor.{ Actor, ActorSystem, Props }
import akka.testkit.{ ImplicitSender, TestKit }
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

class SimpleTest extends TestKit(ActorSystem("TestSpec")) with WordSpecLike with Matchers with BeforeAndAfterAll {
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "Queue actor" must {

    "create queue" in {

      val sumActor = system.actorOf(Props(classOf[QueueCreator]), "summingactor")

      sumActor ! QueueCommands.QueueCreateCommand

    }
  }

}