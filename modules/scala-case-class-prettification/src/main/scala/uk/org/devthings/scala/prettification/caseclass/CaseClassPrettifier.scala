package uk.org.devthings.scala.prettification.caseclass

import scala.annotation.tailrec
import scala.collection.mutable

object CaseClassPrettifier {
  implicit class StringExtension(s: String) {

    def leftIndent(size: Int): String = {
      val padding = " ".padTo(size, " ").mkString

      padding + s
        .split("\n")
        .map { line =>
          padding + line
        }
        .mkString("\n")
        .trim
    }
  }

  def shouldBeUsedInTestMatching(v: Any): Boolean = {
    classIsNonIterableProduct(v) || classIsIteratorContainingCaseClasses(v)
  }

  /** Collections are case classes
    * @param v
    * @return
    */
  private def classIsNonIterableProduct(v: Any): Boolean = {
    Option(v).exists {
      case _: Iterable[_] =>
        false
      case _ =>
        classIsProduct(v)
    }
  }

  private def classIsProduct(v: Any): Boolean = {
    classAsMaybeProduct(v).isDefined
  }

  private def classAsMaybeProduct(v: Any): Option[Product] = {
    v match {
      case vAsProduct: Product => Some(vAsProduct)
      case _ => None
    }
  }

  private def classIsIteratorContainingCaseClasses(v: Any): Boolean = {
    Option(v).collect {
      case iterable: Iterable[_] if iterable.nonEmpty && classIsNonIterableProduct(iterable.head) =>
        true
    }.isDefined
  }

  private def analyze(item: Product): (String, List[String]) = {
    val clazz = item.getClass
    val product = item

    val classNameWithoutPackage: String =
      clazz.getName.replaceAll("(.*)\\.", "")

    classNameWithoutPackage -> product.productElementNames.toList
  }

}

class CaseClassPrettifier {

  import CaseClassPrettifier._

  private implicit class ExtendString(value: AnyRef) {
    def quotify = s""""$value""""
  }

  def prettify(instance: AnyRef): String = {

    def prettifyCollection(className: String, instances: Iterable[_]): String = {
      s"""
         |${className}(
         |${prettifyRecursive("", instances.toList).leftIndent(2)}
         |)
         |""".stripMargin.trim

    }

    instance match {
      // Scala being kooky, Array is passable as Iterable but not matchable as iterable, probably casts on pass
      case instances: Iterable[_] =>
        prettifyCollection(matchToIterableType(instances), instances)

      case instances: Array[_] =>
        prettifyCollection(matchToIterableType(instances), instances)

      case _ => prettifyRecursive("", List(instance))
    }
  }

  @tailrec // try and keep the stack down for scalatest. recursion using head tail requires list not vector
  private def prettifyRecursive(current: String, items: List[Any]): String = {
    items match {
      case head :: tail =>
        prettifyRecursive(current + prettifySingleItem(head) + ",\n", tail)
      case _ =>
        if (current != "") {
          current.stripSuffix(",\n")
        } else {
          current
        }
    }
  }

  private def matchToIterableType(iterable: Iterable[_]): String = {
    iterable match {
      case _: List[_] => "List"
      case _: Vector[_] => "Vector"
      case _: Seq[_] => "Seq"
      case _: mutable.ArraySeq[_] => "mutable.ArraySeq"
      case _: mutable.Seq[_] => "mutable.Seq"
      case _ => "Iterable"
    }
  }

  private def prettifySingleItem(item: Any) = {
    @tailrec
    def iterateFields(remaingFields: List[String], result: List[String] = List()): List[String] = {
      remaingFields match {
        case head :: tail =>
          val method = item.getClass.getDeclaredField(head)
          method.setAccessible(true)
          val value: AnyRef = method.get(item)
          val convertedField = convertSingleFieldValue(head, value)
          iterateFields(tail, result :+ convertedField)

        case _ =>
          result
      }
    }

    classAsMaybeProduct(item)
      .map { (product: Product) =>
        val analyzedResult = analyze(product)
        val fields = analyzedResult._2
          .filter(!_.contains("$"))

        val body = iterateFields(fields)
          .mkString(",\n")

        analyzedResult._1 +
          s"""(
             |${body.leftIndent(2)}
             |)""".stripMargin
      }
      .getOrElse(convertSimple(item))
  }

  private def convertSimple(value: Any): String = value match {
    case string: String =>
      string.quotify

    case _ =>
      if (value != null)
        value.toString
      else
        "null"
  }

  private def convertSingleFieldValue(fieldName: String, value: AnyRef) = {
    val wrapInField = (textValue: String) => s"$fieldName = $textValue"

    value match {
      case None =>
        wrapInField("None")

      case Some(optionValue: AnyRef) if CaseClassPrettifier.classIsProduct(optionValue) =>
        wrapInField(s"""
                       |Some(
                       |${prettify(optionValue).leftIndent(2)}
                       |)
            """.stripMargin.trim)

      case Some(value: Any) =>
        wrapInField(s"""
                       |Some(${value.toString})
            """.stripMargin.trim)

      case _ if CaseClassPrettifier.classIsProduct(value) =>
        wrapInField(prettify(value))

      case _ => wrapInField(convertSimple(value))
    }
  }

}
