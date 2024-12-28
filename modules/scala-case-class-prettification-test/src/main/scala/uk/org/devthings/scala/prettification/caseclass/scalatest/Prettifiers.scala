package uk.org.devthings.scala.prettification.caseclass.scalatest

import org.scalactic.Prettifier
import uk.org.devthings.scala.prettification.caseclass.CaseClassPrettifier

object Prettifiers {
  private val caseClassPrettifier: CaseClassPrettifier = CaseClassPrettifier.default

  implicit val prettifier: Prettifier = Prettifier.apply {
    case anyRef: AnyRef =>
      caseClassPrettifier.prettify(anyRef)

    case anythingElse =>
      Prettifier.default(anythingElse)
  }
}
