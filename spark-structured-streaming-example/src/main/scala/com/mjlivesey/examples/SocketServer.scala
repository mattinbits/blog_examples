package com.mjlivesey.examples

import java.io.PrintStream
import java.net.ServerSocket


object SocketServer extends App {

  val sentences = Array(
    "the cat sat on the mat",
    "to be or not to be",
    "what's the story morning glory"
  )
  val iterator = Iterator.continually(sentences).flatten

  val server = new ServerSocket(9999)
  val s = server.accept()
  val out = new PrintStream(s.getOutputStream())
  while (true) {
    out.println(iterator.next())
    Thread.sleep(100)
  }
}
