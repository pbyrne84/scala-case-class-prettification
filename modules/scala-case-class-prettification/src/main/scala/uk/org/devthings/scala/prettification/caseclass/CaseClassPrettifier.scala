package uk.org.devthings.scala.prettification.caseclass

import uk.org.devthings.scala.prettification.caseclass.action.{
  CaseClassPrettifierAction,
  CharacterPrettifierAction,
  IterablePrettifierAction,
  PrettificationAction,
  TemporalPrettifierAction
}

import scala.annotation.tailrec

object CaseClassPrettifier {
  def create(prettifiers: List[PrettificationAction] = List.empty): CaseClassPrettifier = {
    new CaseClassPrettifier(
      List(
        new IterablePrettifierAction(),
        new TemporalPrettifierAction(),
        new CharacterPrettifierAction(),
        new CaseClassPrettifierAction()
      ) ++ prettifiers
    )
  }

  val default: CaseClassPrettifier = create()
}

class CaseClassPrettifier(prettifiers: List[PrettificationAction]) {

  def prettify(value: Any): String = {
    if (value == null) {
      "null"
    } else {
      prettifiers match {
        case ::(currentPrettifier, remainingPrettifiers) =>
          attemptCurrentPrettifier(value, currentPrettifier, remainingPrettifiers)
        case Nil =>
          value.toString
      }
    }
  }

  @tailrec
  private def attemptCurrentPrettifier(
      value: Any,
      currentPrettifier: PrettificationAction,
      remaining: List[PrettificationAction]
  ): String = {
    currentPrettifier.attempt(value, this) match {
      case Some(renderedOutput: String) => renderedOutput
      case None =>
        remaining match {
          case ::(head: PrettificationAction, next) =>
            attemptCurrentPrettifier(value, head, next)
          case Nil =>
            value.toString
        }
    }
  }

}
