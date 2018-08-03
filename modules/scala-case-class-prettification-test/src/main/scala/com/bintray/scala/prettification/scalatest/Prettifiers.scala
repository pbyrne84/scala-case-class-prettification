package com.bintray.scala.prettification.scalatest

import com.bintray.scala.prettification.CaseClassPrettifier
import org.scalactic.Prettifier

object Prettifiers {
  implicit val prettifier: Prettifier = Prettifier.apply {
    case a: AnyRef if CaseClassPrettifier.shouldBeUsedInTestMatching(a) =>
      new CaseClassPrettifier().prettify(a)

    case a: Any => Prettifier.default(a)
  }
}
