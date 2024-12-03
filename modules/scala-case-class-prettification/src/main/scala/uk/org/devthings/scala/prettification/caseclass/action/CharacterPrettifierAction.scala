package uk.org.devthings.scala.prettification.caseclass.action

import uk.org.devthings.scala.prettification.caseclass.CaseClassPrettifier

class CharacterPrettifierAction extends PrettificationAction {
  override def attempt(currentIndent: Int, value: Any, prettifier2: CaseClassPrettifier): Option[String] = {

    value match {
      case string: String =>
        Some(s""""$string"""")

      case char: Char =>
        Some(s"'$char'")

      case _ =>
        None
    }
  }

}
