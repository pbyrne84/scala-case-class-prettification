package uk.org.devthings.scala.prettification.caseclass.action

import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers
import uk.org.devthings.scala.prettification.caseclass.CaseClassPrettifier

import java.time.{Clock, Instant, LocalDate, LocalDateTime, ZoneId, ZonedDateTime}

class TemporalPrettifierActionSpec extends AnyFreeSpecLike with Matchers with MockFactory {

  private val dateAction = new TemporalPrettifierAction
  private lazy val caseClassPrettifier: CaseClassPrettifier = CaseClassPrettifier.create()

  "attempt" - {
    "should render LocalDate" in {
      val maybeRendered = dateAction.attempt(LocalDate.EPOCH, caseClassPrettifier)
      maybeRendered shouldBe Some("""
          |LocalDate.parse("1970-01-01")
          |""".stripMargin.trim)
    }

    "should render LocalDateDateTime" in {
      val maybeRendered = dateAction.attempt(LocalDateTime.MIN, caseClassPrettifier)
      maybeRendered shouldBe Some("""
          |LocalDateTime.parse("-999999999-01-01T00:00")
          |""".stripMargin.trim)
    }

    "should render Instant" in {
      val maybeRendered = dateAction.attempt(Instant.EPOCH, caseClassPrettifier)
      maybeRendered shouldBe Some("""
          |Instant.parse("1970-01-01T00:00:00Z")
          |""".stripMargin.trim)
    }

    "should render ZonedDateTime" in {
      val clock = mock[Clock]
      (() => clock.instant)
        .expects()
        .returning(Instant.EPOCH)

      (() => clock.getZone)
        .expects()
        .returning(ZoneId.of("UTC"))

      val zonedDateTime = ZonedDateTime.now(clock)
      val maybeRendered = dateAction.attempt(zonedDateTime, caseClassPrettifier)
      maybeRendered shouldBe Some("""
          |ZonedDateTime.parse("1970-01-01T00:00Z[UTC]")
          |""".stripMargin.trim)
    }

  }

}
