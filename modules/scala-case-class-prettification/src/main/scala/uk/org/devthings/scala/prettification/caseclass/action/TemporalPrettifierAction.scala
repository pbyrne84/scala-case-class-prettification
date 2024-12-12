package uk.org.devthings.scala.prettification.caseclass.action

import uk.org.devthings.scala.prettification.caseclass.CaseClassPrettifier

import java.time.{Instant, LocalDate, LocalDateTime, ZonedDateTime}

class TemporalPrettifierAction extends PrettificationAction {
  override def attempt(value: Any, prettifier: CaseClassPrettifier): Option[String] = {

    value match {
      case localDate: LocalDate => createSuccess("LocalDate", localDate.toString)
      case localDateTime: LocalDateTime => createSuccess("LocalDateTime", localDateTime.toString)
      case instant: Instant => createSuccess("Instant", instant.toString)
      case zonedDateTime: ZonedDateTime => createSuccess("ZonedDateTime", zonedDateTime.toString)

      case _ => None
    }
  }

  private def createSuccess(className: String, value: String): Option[String] = Some(
    s"""
       |$className.parse("$value")
       |""".stripMargin.trim
  )
}
