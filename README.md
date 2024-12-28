# Scala case class print prettification

[![scala-case-class-prettification Scala version support](https://index.scala-lang.org/pbyrne84/scala-case-class-prettification/scala-case-class-prettification/latest.svg)](https://index.scala-lang.org/pbyrne84/scala-case-class-prettification/scala-case-class-prettification)

```scala
libraryDependencies += "uk.org.devthings" %% "scala-case-class-prettification" % "{version-from-above}"

// This is pretty simple and just for scalatest
libraryDependencies += "uk.org.devthings" %% "scala-case-class-prettification-test" % "{version-from-above}" % Test
```

A very simple dependency that prettifies case classes into a more readable format. This includes adding the field name, wrapping string in quotes and formatting in a way that keeps nested things readable.

When combined with an implicit org.scalactic.Prettifier such as the one bundled in the test submodule as
a working example it is much easier to spot which field mismatched in test failures.

e.g
```scala
import uk.org.devthings.scala.prettification.caseclass.CaseClassPrettifier
import org.scalactic.Prettifier

object Prettifiers {
  private val caseClassPrettifier: CaseClassPrettifier = CaseClassPrettifier.default

  implicit val prettifier: Prettifier = Prettifier.apply {
    case anyRef: AnyRef =>
      caseClassPrettifier.prettify(anyRef)

    case anythingElse =>
      Prettifier.default(anythingElse)
  }
}
```

A trait version could be made instead if you desire the greater reach inheritance allows.

Intellij's
```
<Click to see difference>
```
Now is more friendly on failure.

Examples are in the test cases

[CaseClassPrettifierTest](https://github.com/pbyrne84/scala-case-class-prettification/blob/master/modules/scala-case-class-prettification/src/test/scala/uk/org/devthings/scala/prettification/caseclass/CaseClassPrettifierTest.scala)

It doesn't try to format too clever as the format needs to be the same across comparisons whatever the values.


### Customising prettifiers

The prettifiers are chained together and implement the following **PrettificationAction** trait

```scala
package uk.org.devthings.scala.prettification.caseclass.action

import uk.org.devthings.scala.prettification.caseclass.CaseClassPrettifier

sealed trait PrettificationAttemptResult
case object SkippedPrettificationAsNotRelevant extends PrettificationAttemptResult
case class SuccessfulPrettification(result: String) extends PrettificationAttemptResult

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

  def attempt(value: Any, prettifier: CaseClassPrettifier): PrettificationAttemptResult
}
```

If the action determines that it is not the correct one for the passed value, it should return **SkippedPrettificationAsNotRelevant**. 
This triggers the process to try the next action if there is one. This allows easy testing of each action and 
keeps things nicely separated. 

The default implementation handles the following cases.
```scala
object CaseClassPrettifier {
  val default: CaseClassPrettifier = create(
    List(
      new IterablePrettifierAction(),
      new TemporalPrettifierAction(),
      new CharacterPrettifierAction(),
      new CaseClassPrettifierAction()
    )
  )

  def create(prettifiers: List[PrettificationAction] = List.empty): CaseClassPrettifier = {
    new CaseClassPrettifier(prettifiers)
  }
}
```

**TemporalPrettifierAction** prints out dates in a constructional format, e.g. LocalDate.parse("1970-01-01").
Sometimes we get in situations where we inhabit projects where things like mocking are not done well, wildcards 
being used for parameters where it should not be. Mocking is about testing two-way communication, wild cards negate
this to one way leaving potentially highly nasty holes in the test while getting line coverage. You can use the prettifier to
dump things out in a way we can do things like tighten mocks, etc.


```scala
class TemporalPrettifierAction extends PrettificationAction {
  override def attempt(value: Any, prettifier: CaseClassPrettifier): PrettificationAttemptResult = {

    value match {
      case localDate: LocalDate => createSuccess("LocalDate", localDate.toString)
      case localDateTime: LocalDateTime => createSuccess("LocalDateTime", localDateTime.toString)
      case instant: Instant => createSuccess("Instant", instant.toString)
      case zonedDateTime: ZonedDateTime => createSuccess("ZonedDateTime", zonedDateTime.toString)

      case _ =>
        SkippedPrettificationAsNotRelevant
    }
  }

  private def createSuccess(className: String, value: String): SuccessfulPrettification =
    SuccessfulPrettification(
      s"""
       |$className.parse("$value")
       |""".stripMargin.trim
    )
}
```

## The diff part
This is simply a wrapper for the command land diff handling of Intellij. If you have the command line launcher set up then calling
```
idea /path/to/filename
```
will open the file in Intellij as a tab


```
idea diff /path/to/filename1 /path/to/filename2
```
will open the file in intellij diff.


So by simply creating a temp file or temp files we can view things in a less head ache inducing environment. Testing should be fun, diffing complex things that are on a single line is not so fun.

## Standard toString on case classes can be confusing.
As strings are not wrapped in quotes it can be confusing. A comma in a value can obfuscate what
the column index is.

e.g. A simple example.
```scala
case class A(a: String, b: String, c: String)
case class B(a: String, c: String)

A("a", "b", "c").toString == "A(a,b,c)"
B("a,b", "c").toString == "B(a,b,c)"

```

After prettification these become
```
A(
  a = "a",
  b = "b",
  c = "c"
)
```

and
```
B(
  a = "a,b",
  c = "c"
)
```

respectively, so the diff is greater in variance than just the class name.
