package uk.org.devthings.scala.prettification.caseclass

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable
case class SinglePrimitive(fieldName1: Int)
case class ListOfPrimitives(fieldName1: List[Int])
case class MultiBasic(fieldName1: Int, fieldName2: String)
case class NestedBasic(fieldName1: Int, fieldName2: SinglePrimitive)
case class NestedOptionalCaseClass(fieldName1: Int, fieldName2: Option[SinglePrimitive])
case class NestedOptionalInt(fieldName1: Int, fieldName2: Option[Int])
case class NestedMultiLevel(fieldName1: Int, fieldName2: NestedBasic)

sealed abstract case class AbstractCaseClass(value: Int, value2: NestedOptionalCaseClass)

class CaseClassPrettifierTest extends AnyWordSpec with Matchers {

  "GenericPrettifier" should {

    val prettifier2 = CaseClassPrettifier.create()

    "handle a null safely for badly behaving apis" in {
      prettifier2.prettify(null) shouldBe
        """
          |null
        """.stripMargin.trim
    }

    "format a list with 1 item which is an int" in {
      prettifier2.prettify(List(4)) shouldBe
        """
          |List(
          |  4
          |)
        """.stripMargin.trim
    }

    "format a list with 1 item which is a string" in {
      prettifier2.prettify(List("banana")) shouldBe
        """
          |List(
          |  "banana"
          |)
        """.stripMargin.trim
    }

    "format a list with multiple items" in {
      prettifier2.prettify(List(4, 5, 6)) shouldBe
        """
          |List(
          |  4,
          |  5,
          |  6
          |)
        """.stripMargin.trim
    }

    "format a vector with multiple items" in {
      prettifier2.prettify(Vector(4, 5, 6)) shouldBe
        """
          |Vector(
          |  4,
          |  5,
          |  6
          |)
        """.stripMargin.trim
    }

    "format an mutable array with multiple items" in {
      prettifier2.prettify(Array(4, 5, 6)) shouldBe
        """
          |mutable.ArraySeq(
          |  4,
          |  5,
          |  6
          |)
        """.stripMargin.trim
    }

    "format an mutable seq with multiple items" in {
      prettifier2.prettify(mutable.Seq(4, 5, 6)) shouldBe
        """
          |mutable.Seq(
          |  4,
          |  5,
          |  6
          |)
        """.stripMargin.trim
    }

    "format a simple case class with a single primitive" in {
      val str = prettifier2.prettify(SinglePrimitive(fieldName1 = 4))
      str shouldBe
        """
          |SinglePrimitive(
          |  fieldName1 = 4
          |)
        """.stripMargin.trim
    }

    "format a list of simple case class with a single primitive" in {
      prettifier2.prettify(List(SinglePrimitive(4))) shouldBe
        """
          |List(
          |  SinglePrimitive(
          |    fieldName1 = 4
          |  )
          |)
        """.stripMargin.trim
    }

    "format a simple case class with a list of primitives" in {
      prettifier2.prettify(ListOfPrimitives(List(1, 4))) shouldBe
        """
          |ListOfPrimitives(
          |  fieldName1 = List(
          |    1,
          |    4
          |  )
          |)
        """.stripMargin.trim
    }

    "format a simple case class multiple single values" in {
      prettifier2.prettify(MultiBasic(4, "bananana"))
      """
        |MultiBasic(
        |  fieldName1 = 4,
        |  fieldName2 = "bananana"
        |)
      """.stripMargin.trim
    }

    "format a simple case class multiple single values where one value is null" in {
      prettifier2.prettify(MultiBasic(4, null)) shouldBe
        """
          |MultiBasic(
          |  fieldName1 = 4,
          |  fieldName2 = null
          |)
        """.stripMargin.trim
    }

    "format a simple nested case class multiple single values" in {
      prettifier2.prettify(NestedBasic(4, SinglePrimitive(4))) shouldBe
        """
          |NestedBasic(
          |  fieldName1 = 4,
          |  fieldName2 = SinglePrimitive(
          |    fieldName1 = 4
          |  )
          |)
        """.stripMargin.trim
    }

    "format a simple nested case class where one value is an option with a case class as a value" in {
      val actual = prettifier2.prettify(NestedOptionalCaseClass(4, Some(SinglePrimitive(4))))
      actual shouldBe
        """
          |NestedOptionalCaseClass(
          |  fieldName1 = 4,
          |  fieldName2 = Some(
          |    SinglePrimitive(
          |      fieldName1 = 4
          |    )
          |  )
          |)
        """.stripMargin.trim
    }

    "format a simple nested case class where one value is an optional int" in {
      val actual = prettifier2.prettify(NestedOptionalInt(4, Some(6)))
      println(actual)
      actual shouldBe
        """
          |NestedOptionalInt(
          |  fieldName1 = 4,
          |  fieldName2 = Some(6)
          |)
        """.stripMargin.trim
    }

    "format a simple nested case class where one value is an option with a value of None" in {
      prettifier2.prettify(NestedOptionalCaseClass(4, None)) shouldBe
        """
          |NestedOptionalCaseClass(
          |  fieldName1 = 4,
          |  fieldName2 = None
          |)
        """.stripMargin.trim
    }

    "handle multi level indentation" in {
      val result =
        prettifier2.prettify(NestedMultiLevel(4, NestedBasic(4, SinglePrimitive(4))))

      result shouldBe
        """
          |NestedMultiLevel(
          |  fieldName1 = 4,
          |  fieldName2 = NestedBasic(
          |    fieldName1 = 4,
          |    fieldName2 = SinglePrimitive(
          |      fieldName1 = 4
          |    )
          |  )
          |)
        """.stripMargin.trim
    }

    "handle a list of tuples" in {
      val result =
        prettifier2.prettify(List("a" -> 2, "b" -> "44"))

      result shouldBe
        """
          |List(
          |  Tuple2(
          |    _1 = "a",
          |    _2 = 2
          |  ),
          |  Tuple2(
          |    _1 = "b",
          |    _2 = "44"
          |  )
          |)
        """.stripMargin.trim
    }

    "cope with instances of abstract case class" in {
      val instantiatingBlockingCaseClass = new AbstractCaseClass(22, NestedOptionalCaseClass(4, None)) {}
      val result = prettifier2.prettify(instantiatingBlockingCaseClass)

      result shouldBe
        """
          |AbstractCaseClass(
          |  value = 22,
          |  value2 = NestedOptionalCaseClass(
          |    fieldName1 = 4,
          |    fieldName2 = None
          |  )
          |)
        """.stripMargin.trim

    }
  }

}
