package com.mjlivesey.akkasemantics

import akka.actor.{Props, ActorRef}

trait SendReceive {

  def getReceiver: Props
  def getSender(receiver: ActorRef): Props
}
