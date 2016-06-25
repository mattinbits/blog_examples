package com.mjlivesey.mutableakka

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object RemoteExample extends App {

  val role = args(0)
  val senderPort = args(1)
  val receiverPort = args(2)

  val port = role match {
    case "sender" => senderPort
    case "receiver" => receiverPort
  }

  val defaultConfig = ConfigFactory.load()
  val customPortConfig = ConfigFactory.parseString(s"akka.remote.netty.tcp.port = $port")
  val system = ActorSystem(role, customPortConfig.withFallback(defaultConfig))

  role match {
    case "sender" =>
      system.
        actorSelection(s"akka.tcp://receiver@127.0.0.1:${receiverPort}/user/receiver").
        resolveOne(3 seconds).foreach{receiver =>
          val sender = system.actorOf(Props[Sender])
          sender ! Start(receiver)
        }


    case "receiver" => system.actorOf(Props[Receiver], "receiver")
  }
}
