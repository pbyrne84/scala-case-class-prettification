package uk.org.devthings.scala.prettification.caseclass.action

import uk.org.devthings.scala.prettification.caseclass.CaseClassPrettifier

class CaseClassPrettifierAction extends PrettificationAction {

  override def attempt(value: Any, prettifier: CaseClassPrettifier): Option[String] = {
    value match {
      case option: Some[_] =>
        val prettifiedContents: String = createSomeResult(prettifier, option)

        Some(
          s"""
          |Some($prettifiedContents)
          |""".stripMargin.trim
        )

      case None =>
        Some("None")

      case product: Product =>
        val renderedFields = for (index <- 0 until product.productArity) yield {
          val name = product.productElementName(index)
          val elementValue = product.productElement(index)
          s"$name = ${prettifier.prettify(elementValue)}"
        }

        Some(
          s"""${calculateClassName(product)}(
           |${renderedFields.mkString(",\n").stripSuffix(",\n").leftIndent(2)}
           |)""".stripMargin
        )

      case _ => None
    }
  }

  private def calculateClassName(product: Product) = {
    if (product.getClass.isAnonymousClass) {
      product.getClass.getSuperclass.getSimpleName
    } else {
      product.getClass.getSimpleName
    }
  }

  private def createSomeResult(prettifier2: CaseClassPrettifier, option: Some[_]): String = {
    val prettifiedContents = prettifier2.prettify(option.value)

    option.value match {
      case _: Product =>
        s"\n${prettifiedContents.leftIndent(2)}\n"
      case _ =>
        prettifiedContents

    }

  }
}
