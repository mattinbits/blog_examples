package com.mjlivesey.akkasemantics

import java.util.concurrent.ConcurrentLinkedQueue

import akka.actor.{ActorRef, ActorSystem}
import akka.dispatch.{Envelope, MailboxType, MessageQueue, ProducesMessageQueue}
import com.typesafe.config.Config

import scala.util.Random

// Marker trait used for mailbox requirements mapping
trait UnreliableMailboxSemantics

object UnreliableMailbox {

  class MyMessageQueue extends MessageQueue
  with UnreliableMailboxSemantics {
    private final val queue = new ConcurrentLinkedQueue[Envelope]()

    // 5% of messages are lost. Unless we're sending them to ourself
    def enqueue(receiver: ActorRef, handle: Envelope): Unit = {
      if(Random.nextDouble > 0.05 || receiver == handle.sender)
      queue.offer(handle)
    }

    def dequeue(): Envelope = queue.poll()

    def numberOfMessages: Int = queue.size

    def hasMessages: Boolean = !queue.isEmpty

    def cleanUp(owner: ActorRef, deadLetters: MessageQueue) {
      while (hasMessages) {
        deadLetters.enqueue(owner, dequeue())
      }
    }
  }
}

// This is the Mailbox implementation
class UnreliableMailbox extends MailboxType
with ProducesMessageQueue[UnreliableMailbox.MyMessageQueue] {

  import UnreliableMailbox._

  def this(settings: ActorSystem.Settings, config: Config) = {
    this()
  }

  final override def create(owner: Option[ActorRef],
                            system: Option[ActorSystem]): MessageQueue =
    new MyMessageQueue()
}
