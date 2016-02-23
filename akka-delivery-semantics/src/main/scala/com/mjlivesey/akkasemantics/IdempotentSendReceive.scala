package com.mjlivesey.akkasemantics

import akka.actor.{Props, ActorRef, Actor, ActorLogging}
import com.mjlivesey.akkasemantics.AckSendReceive.{Finished, Ack, CountMe}

object IdempotentSendReceive extends SendReceive {

  class Receiver extends Actor with ActorLogging {

    var receipts: Map[Int, Boolean] = (1 to 100).map(i => i -> false).toMap

    def countingReceiver(counter: Int): Receive = {

      case CountMe(i) =>
        log.info(s"Received message number ${i}")
        receipts = receipts + (i -> true)
        sender ! Ack(i)
        context.become(countingReceiver(counter+1))

      case Finished =>
        log.info(s"Receiver received ${receipts.values.count(_ == true)} distinct messages.")
        context.system.terminate()

      case other => log.error(s"Received ${other}")
    }

    def receive = countingReceiver(0)
  }

  override def getReceiver: Props = Props[Receiver]

  override def getSender(receiver: ActorRef): Props = AckSendReceive.getSender(receiver)
}
