package com.erkan.test

import com.erkan._
import akka.actor._
import akka.stream.ActorMaterializer
import akka.testkit.{ ImplicitSender, TestKit }
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }
import scala.concurrent.duration._

class SimpleTest extends TestKit(ActorSystem("TestSpec")) with WordSpecLike with Matchers with BeforeAndAfterAll {
  override def afterAll {
    TestKit.shutdownActorSystem(system,  verifySystemShutdown = false)

  }



  "Queue actor" must {

    "create queue" in {

      val actor = system.actorOf(Props(classOf[QueueCreator]),"test")

      actor ! QueueCommands.QueueCreateCommand

    }

   
  }

}