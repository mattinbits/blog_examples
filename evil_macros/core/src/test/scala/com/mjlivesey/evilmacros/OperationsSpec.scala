package com.mjlivesey.evilmacros

import org.scalatest.{Matchers, WordSpec}

class OperationsSpec extends WordSpec with Matchers {

  import Operations._

  "Operations" should {

    "Add Two Numbers" in {
      addTwoNumbers(3, 4) should be (7)
    }

    "Find the max of three numbers" in {
      maxOfThreeNumbers(12, 23, 20) should be (23)
    }

    "Find the length of a string" in {
      lengthOfString("abc") should be (3)
    }
  }
}
