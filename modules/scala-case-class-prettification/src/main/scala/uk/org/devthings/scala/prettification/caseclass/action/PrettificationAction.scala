package uk.org.devthings.scala.prettification.caseclass.action

import uk.org.devthings.scala.prettification.caseclass.CaseClassPrettifier

trait PrettificationAction {

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

  def attempt(value: Any, prettifier: CaseClassPrettifier): Option[String]
}
