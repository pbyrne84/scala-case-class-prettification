package uk.org.devthings.scala.prettification.caseclass.scalatest

import org.scalatest.exceptions.TestFailedException
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class PrettifiersSpec extends AnyWordSpecLike with Matchers {

  import Prettifiers.prettifier

  "scalatest prettification" should {

    "not fail with a match error on nulls that sneak past signatures" in {
      val badlyBehavingOptionalString: Option[String] = null

      a[TestFailedException] should be thrownBy {
        badlyBehavingOptionalString shouldBe None
      }
    }
  }
}
