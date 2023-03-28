# Scala case class print prettification

A very simple dependency that prettifies case classes into a more readable format. This includes adding the field name, wrapping string in quotes and formatting in a way that keeps nested things readable.

When combined with an implicit org.scalactic.Prettifier such as the one bundled in the test submodule as
a working example it is much easier to spot which field mismatched in test failures.

e.g
```scala
import uk.org.devthings.scala.prettification.caseclass.CaseClassPrettifier
import org.scalactic.Prettifier

object Prettifiers {
  implicit val prettifier: Prettifier = Prettifier.apply {
    case a: AnyRef if CaseClassPrettifier.shouldBeUsedInTestMatching(a) =>
      new CaseClassPrettifier().prettify(a)

    case a: Any => Prettifier.default(a)
  }
}
```

A trait version could be made instead if you desire the greater reach inheritance
allows.

Intellij's
```
<Click to see difference>
```
Now is more friendly on failure.

Examples are in the test cases

[CaseClassPrettifierTest](modules/scala-case-class-prettification/src/test/scala/com/bintray/scala/prettification/CaseClassPrettifierTest.scala)

It doesn't try to format too clever as the format needs to be the same across comparisons whatever the values.


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

respectively so the diff is greater in variance than just the class name.
