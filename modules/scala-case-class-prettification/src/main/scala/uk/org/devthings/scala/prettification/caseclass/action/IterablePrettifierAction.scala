package uk.org.devthings.scala.prettification.caseclass.action

import uk.org.devthings.scala.prettification.caseclass.CaseClassPrettifier

import scala.collection.mutable

class IterablePrettifierAction extends PrettificationAction {
  override def attempt(value: Any, prettifier: CaseClassPrettifier): Option[String] = {

    value match {
      case iterable: Iterable[_] =>
        val stringType = getStringType(iterable)

        val renderedItems = iterable
          .map { item =>
            prettifier.prettify(item)
          }
          .mkString(",\n")
          .leftIndent(2)

        Some(s"$stringType(\n$renderedItems\n)")

      case instances: Array[_] =>
        val stringType = getStringType(instances)

        val renderedItems = instances.toList
          .map { item =>
            prettifier.prettify(item)
          }
          .mkString(",\n")
          .leftIndent(2)

        Some(s"$stringType(\n$renderedItems\n)")

      case _ => None
    }
  }

  private def getStringType(iterable: Iterable[_]): String =
    iterable match {
      case _: List[_] => "List"
      case _: Vector[_] => "Vector"
      case _: Seq[_] => "Seq"
      case _: mutable.ArraySeq[_] => "mutable.ArraySeq"
      case _: mutable.Seq[_] => "mutable.Seq"
      case _ => "Iterable"
    }
}
