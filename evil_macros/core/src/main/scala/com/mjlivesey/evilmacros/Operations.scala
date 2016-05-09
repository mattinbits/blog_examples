package com.mjlivesey.evilmacros

@evil
object Operations {

  def addTwoNumbers(a: Int, b: Int): Int = a + b

  def maxOfThreeNumbers(a:Int, b: Int, c: Int): Int = math.max(math.max(a, b), c)

  def lengthOfString(s: String): Int = s.length
}
