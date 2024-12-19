package uk.org.devthings.scala.prettification.caseclass.action

import uk.org.devthings.scala.prettification.caseclass.CaseClassPrettifier

class CharacterPrettifierAction extends PrettificationAction {
  override def attempt(value: Any, prettifier: CaseClassPrettifier): PrettificationAttemptResult = {

    value match {
      case string: String =>
        SuccessfulPrettification(s""""$string"""")

      case char: Char =>
        SuccessfulPrettification(s"'$char'")

      case _ =>
        SkippedPrettificationAsNotRelevant
    }
  }

}
