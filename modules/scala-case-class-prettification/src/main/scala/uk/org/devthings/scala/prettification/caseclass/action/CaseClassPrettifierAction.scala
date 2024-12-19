package uk.org.devthings.scala.prettification.caseclass.action

import uk.org.devthings.scala.prettification.caseclass.CaseClassPrettifier

class CaseClassPrettifierAction extends PrettificationAction {

  override def attempt(value: Any, prettifier: CaseClassPrettifier): PrettificationAttemptResult = {
    value match {
      case option: Some[_] =>
        val prettifiedContents: String = createSomeResult(prettifier, option)

        SuccessfulPrettification(
          s"""
          |Some($prettifiedContents)
          |""".stripMargin.trim
        )

      case None =>
        SuccessfulPrettification("None")

      case product: Product =>
        val renderedFields = for (index <- 0 until product.productArity) yield {
          val name = product.productElementName(index)
          val elementValue = product.productElement(index)
          s"$name = ${prettifier.prettify(elementValue)}"
        }

        SuccessfulPrettification(
          s"""${calculateClassName(product)}(
           |${renderedFields.mkString(",\n").stripSuffix(",\n").leftIndent(2)}
           |)""".stripMargin
        )

      case _ =>
        SkippedPrettificationAsNotRelevant
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
