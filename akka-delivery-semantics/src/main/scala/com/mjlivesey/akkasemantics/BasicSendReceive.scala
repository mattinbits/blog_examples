package com.mjlivesey.akkasemantics

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import scala.concurrent.duration._

object BasicSendReceive extends SendReceive {

  case object CountMe
  case object Finished

  class Sender(receiver: ActorRef) extends Actor with ActorLogging {

    import context.dispatcher

    case object Send

    var numSent = 0

    override def preStart(): Unit = {
      scheduleNext()
    }

    def receive = {
      case Send =>
        receiver ! CountMe
        numSent = numSent + 1
        log.info(s"Sent message ${numSent}")
        if(numSent == 100) {
          log.info("Sender sent 100 messages")
          receiver ! Finished
        } else
          scheduleNext()
    }

    def scheduleNext() = context.system.scheduler.scheduleOnce(10 millis, self, Send)
  }

  class Receiver extends Actor with ActorLogging {

    def countingReceiver(current: Int): Receive = {

      case CountMe =>
        log.info(s"Received message number ${current+1}")
        context.become(countingReceiver(current+1))

      case Finished =>
        log.info(s"Receiver received ${current} messages.")
        context.system.terminate()
    }

    def receive = countingReceiver(0)
  }

  override def getReceiver = Props[Receiver]
  override def getSender(receiver: ActorRef) = Props(new Sender(receiver))
}
