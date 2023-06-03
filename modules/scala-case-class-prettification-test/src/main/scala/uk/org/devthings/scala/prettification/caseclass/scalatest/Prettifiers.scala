package uk.org.devthings.scala.prettification.caseclass.scalatest

import org.scalactic.Prettifier
import uk.org.devthings.scala.prettification.caseclass.CaseClassPrettifier

object Prettifiers {
  val caseClassPrettifier: CaseClassPrettifier = new CaseClassPrettifier

  implicit val prettifier: Prettifier = Prettifier.apply {
    case anyRef: AnyRef if CaseClassPrettifier.shouldBeUsedInTestMatching(anyRef) =>
      caseClassPrettifier.prettify(anyRef)

    case anythingElse =>
      Prettifier.default(anythingElse)
  }
}
