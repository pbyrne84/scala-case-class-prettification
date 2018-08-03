package com.bintray.scala.prettification

import org.scalatest.{Matchers, WordSpec}

case class SinglePrimitive(fieldName1: Int)
case class ListOfPrimitives(fieldName1: List[Int])
case class MultiBasic(fieldName1: Int, fieldName2: String)
case class NestedBasic(fieldName1: Int, fieldName2: SinglePrimitive)
case class NestedOptionalCaseClass(fieldName1: Int, fieldName2: Option[SinglePrimitive])
case class NestedOptionalInt(fieldName1: Int, fieldName2: Option[Int])
case class NestedMultiLevel(fieldName1: Int, fieldName2: NestedBasic)

class CaseClassPrettifierTest extends WordSpec with Matchers {

  "GenericPrettifier" should {

    val prettifier = new CaseClassPrettifier()
    "format a list with 1 item which is an int" in {
      prettifier.prettify(List(4)) shouldBe
        """
          |List(
          |  4
          |)
        """.stripMargin.trim
    }

    "format a list with 1 item which is a string" in {
      prettifier.prettify(List("banana")) shouldBe
        """
          |List(
          |  "banana"
          |)
        """.stripMargin.trim
    }

    "format a list with multiple items" in {
      prettifier.prettify(List(4, 5, 6)) shouldBe
        """
          |List(
          |  4,
          |  5,
          |  6
          |)
        """.stripMargin.trim
    }

    "format a simple case class with a single primitive" in {
      val str = prettifier.prettify(SinglePrimitive(4))
      str shouldBe
        """
          |SinglePrimitive(
          |  fieldName1 = 4
          |)
        """.stripMargin.trim
    }

    "format a list of simple case class with a single primitive" in {
      prettifier.prettify(List(SinglePrimitive(4))) shouldBe
        """
          |List(
          |  SinglePrimitive(
          |    fieldName1 = 4
          |  )
          |)
        """.stripMargin.trim
    }

    "format a simple case class with a list of primitives" in {
      prettifier.prettify(ListOfPrimitives(List(1, 4))) shouldBe
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
      prettifier.prettify(MultiBasic(4, "bananana"))
      """
        |MultiBasic(
        |  fieldName1 = 4,
        |  fieldName2 = "bananana"
        |)
      """.stripMargin.trim
    }

    "format a simple case class multiple single values where one value is null" in {
      prettifier.prettify(MultiBasic(4, null)) shouldBe
        """
          |MultiBasic(
          |  fieldName1 = 4,
          |  fieldName2 = null
          |)
        """.stripMargin.trim
    }

    "format a simple nested case class multiple single values" in {
      prettifier.prettify(NestedBasic(4, SinglePrimitive(4))) shouldBe
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
      prettifier.prettify(NestedOptionalCaseClass(4, Some(SinglePrimitive(4)))) shouldBe
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
      prettifier.prettify(NestedOptionalInt(4, Some(6))) shouldBe
        """
          |NestedOptionalInt(
          |  fieldName1 = 4,
          |  fieldName2 = Some(6)
          |)
        """.stripMargin.trim
    }

    "format a simple nested case class where one value is an option with a value of None" in {
      prettifier.prettify(NestedOptionalCaseClass(4, None)) shouldBe
        """
          |NestedOptionalCaseClass(
          |  fieldName1 = 4,
          |  fieldName2 = None
          |)
        """.stripMargin.trim
    }

    "handle mutli level indentation" in {
      val result =
        prettifier.prettify(NestedMultiLevel(4, NestedBasic(4, SinglePrimitive(4))))

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

  }

}
