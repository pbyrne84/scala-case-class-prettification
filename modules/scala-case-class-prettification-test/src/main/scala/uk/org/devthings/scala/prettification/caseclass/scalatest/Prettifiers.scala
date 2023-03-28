package uk.org.devthings.scala.prettification.caseclass.scalatest

import org.scalactic.Prettifier
import uk.org.devthings.scala.prettification.caseclass.CaseClassPrettifier

object Prettifiers {
  val caseClassPrettifier: CaseClassPrettifier = new CaseClassPrettifier

  implicit val prettifier: Prettifier = Prettifier.apply {
    case a: AnyRef if CaseClassPrettifier.shouldBeUsedInTestMatching(a) =>
      caseClassPrettifier.prettify(a)

    case a: Any => Prettifier.default(a)
  }
}
