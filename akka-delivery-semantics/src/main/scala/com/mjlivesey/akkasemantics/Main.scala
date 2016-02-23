package com.mjlivesey.akkasemantics

import akka.actor.{ActorSystem, Props}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/*
  Run a Delivery example. Takes 3 arguments: <senderMailbox> <receiverMailbox> <type>

  the mailboxes are one of 'unreliable' or 'default-mailbox'

  type can be 'basic', 'ack' or 'idempotent'

  e.g.

  sbt 'run ack unreliable unreliable'
 */
object Main extends App {

  val system = ActorSystem("SendReceive")

  val senderMailbox = args(1)
  val receiverMailbox = args(2)

  val builder: SendReceive = args(0) match {
    case "basic" => BasicSendReceive
    case "ack" => AckSendReceive
    case "idempotent" => IdempotentSendReceive
  }
  val receiver = system.actorOf(builder.getReceiver.withMailbox(receiverMailbox))
  val sender = system.actorOf(builder.getSender(receiver).withMailbox(senderMailbox))

  Await.result(system.whenTerminated, Duration.Inf)
}
