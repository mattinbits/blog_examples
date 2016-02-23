package com.mjlivesey.akkasemantics

import akka.actor._
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.{pipe, ask}

import scala.util.{Failure, Success}

object AckSendReceive extends SendReceive {

  case class CountMe(id: Int)
  case class Ack(id: Int)
  case object Finished

  def senderProps(receiver: ActorRef) = Props(new Sender(receiver))


  class Sender(receiver: ActorRef) extends Actor with ActorLogging {

    import context.dispatcher

    case object Send
    case class InternalAck(id: Int)
    case object Timeout


    var numSent = 0
    var retry: Option[Cancellable] = None


    override def preStart(): Unit = {
      scheduleNext()
    }

    def sending(next: Int): Receive = {
      case Send =>
        numSent = numSent + 1
        log.info(s"Sending message ${next}")
        receiver ! CountMe(next)
        val timer = context.system.scheduler.scheduleOnce(50 millis, self, Timeout)
        context.become(waiting(next, timer))
    }

    def waiting(next: Int, timer: Cancellable): Receive = {

      case Ack(i) =>
        timer.cancel()
        log.info(s"Received Ack for message ${next}")
        if (i == 100) {
          log.info(s"Sender sent ${numSent} messages")
          receiver ! Finished
        } else {
          scheduleNext()
          context.become(sending(next+1))
        }

      case Timeout =>
        log.info(s"Did not receive Ack for message ${next}")
        scheduleNext()
        context.become(sending(next))
    }

    def receive = sending(1)

    def scheduleNext() = context.system.scheduler.scheduleOnce(10 millis, self, Send)
  }

  class Receiver extends Actor with ActorLogging {

    def countingReceiver(counter: Int): Receive = {

      case CountMe(i) =>
        log.info(s"Received message number ${i}")
        sender ! Ack(i)
        context.become(countingReceiver(counter+1))

      case Finished =>
        log.info(s"Receiver received ${counter} messages.")
        context.system.terminate()

      case other => log.error(s"Received ${other}")
    }

    def receive = countingReceiver(0)
  }

  override def getReceiver = Props[Receiver]
  override def getSender(receiver: ActorRef) = Props(new Sender(receiver))
}
