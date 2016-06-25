package com.mjlivesey.mutableakka

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._

case class Message(parts: Seq[String])

class Receiver extends Actor {

  import context.dispatcher

  case object DoProcess

  var parts: Option[Seq[String]] = None

  override def receive: Receive = {

    case Message(p)  =>
      parts = Some(p)
      context.system.scheduler.scheduleOnce(5 seconds, self, DoProcess)

    case DoProcess =>
      parts.foreach(_.foreach(println))
      context.system.terminate()
  }
}

case class Start(receiver: ActorRef)

class Sender extends Actor {

  import context.dispatcher

  case object Mutate

  val buffer = ArrayBuffer("A", "B", "C")

  override def receive: Receive = {

    case Start(rec) =>
      rec ! Message(buffer)
      context.system.scheduler.scheduleOnce(1 seconds, self, Mutate)

    case Mutate => buffer += "D"

  }
}

object Example extends App {

  val system = ActorSystem("Example")

  val receiver = system.actorOf(Props[Receiver])
  val sender = system.actorOf(Props[Sender])

  sender ! Start(receiver)
}
