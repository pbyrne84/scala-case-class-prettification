package com.bintray.scala.prettification

import scala.annotation.tailrec

object CaseClassPrettifier {
  private type ImmutableSeq[+A] = scala.collection.immutable.Seq[A]
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
    CaseClassPrettifier.classIsNonIterableCaseClass(v) || CaseClassPrettifier
      .classIsIteratorContainingCaseClasses(v)
  }

  /**
    * Collections are case classes
    * @param v
    * @return
    */
  private def classIsNonIterableCaseClass(v: Any): Boolean = {
    if (v == null) {
      false
    } else {
      v match {
        case _: Iterable[_] => false
        case _ =>
          classIsAnyCaseClass(v)
      }
    }
  }

  private def classIsAnyCaseClass(v: Any): Boolean = {
    if (v == null) {
      false
    } else {
      v match {
        case _: Product => true
        case _ => false
      }
    }
  }

  def classIsIteratorContainingCaseClasses(v: Any): Boolean = {
    v match {
      case iterable: Iterable[_]
          if iterable.nonEmpty && classIsNonIterableCaseClass(iterable.head) =>
        true
      case _ => false
    }
  }

  private def analyze[A](clazz: Class[A]): (String, List[String]) = {
    import scala.reflect.runtime.currentMirror
    val symbol = currentMirror.classSymbol(clazz)
    val primaryConstructor = symbol.primaryConstructor
    val signature = primaryConstructor.typeSignature

    val classNameWithoutPackage: String =
      clazz.getName.replaceAll("(.*)\\.", "")

    classNameWithoutPackage -> signature.paramLists.head.map(_.name.toString)
  }

}

class CaseClassPrettifier {

  import CaseClassPrettifier._

  private implicit class ExtendString(value: AnyRef) {
    def quotify = s""""$value""""
  }

  def prettify(instance: AnyRef): String = {
    instance match {
      case instances: Iterable[_] =>
        s"""
           |${matchToIterableType(instances)}(
           |${prettifyRecursive("", instances.toList).leftIndent(2)}
           |)
           |""".stripMargin.trim

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

  private def matchToIterableType(iterable: Iterable[_]) = {
    iterable match {
      case _: List[_] => "List"
      case _: Vector[_] => "Vector"
      case _: ImmutableSeq[_] => "immutable.Seq"
      case _: Seq[_] => "Seq"
      case _ => "Iterable"
    }

  }

  private def prettifySingleItem(item: Any) = {
    if (CaseClassPrettifier.classIsAnyCaseClass(item)) {
      val anaylzedResult = analyze(item.getClass)
      val fields = anaylzedResult._2
        .filter(!_.contains("$"))

      @tailrec
      def iterateFields(remaingFields: List[String],
                        result: List[String] = List()): List[String] = {
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

      val body = iterateFields(fields)
        .mkString(",\n")

      anaylzedResult._1 +
        s"""(
           |${body.leftIndent(2)}
           |)""".stripMargin
    } else {
      convertSimple(item)
    }
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

      case Some(optionValue: AnyRef) if CaseClassPrettifier.classIsAnyCaseClass(optionValue) =>
        wrapInField(s"""
                       |Some(
                       |${prettify(optionValue).leftIndent(2)}
                       |)
            """.stripMargin.trim)

      case Some(value: Any) =>
        wrapInField(s"""
                       |Some(${value.toString})
            """.stripMargin.trim)

      case _ if CaseClassPrettifier.classIsAnyCaseClass(value) =>
        wrapInField(prettify(value))

      case _ => wrapInField(convertSimple(value))
    }
  }

}
