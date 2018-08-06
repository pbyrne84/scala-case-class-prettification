package com.bintray.scala.prettification.scalatest

import com.bintray.scala.prettification.CaseClassPrettifier
import org.scalactic.Prettifier

object Prettifiers {
  val caseClassPrettifier: CaseClassPrettifier = new CaseClassPrettifier

  implicit val prettifier: Prettifier = Prettifier.apply {
    case a: AnyRef if CaseClassPrettifier.shouldBeUsedInTestMatching(a) =>
      caseClassPrettifier.prettify(a)

    case a: Any => Prettifier.default(a)
  }
}
