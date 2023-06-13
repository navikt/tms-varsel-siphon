package no.nav.tms.varsel.siphon

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

object LocalDateTimeHelper {
    fun nowAtUtc() = LocalDateTime.now((ZoneId.of("Z"))).truncatedTo(ChronoUnit.MILLIS)

}

object ZonedDateTimeHelper {
    fun nowAtUtcZ() = ZonedDateTime.now(ZoneId.of("Z")).truncatedTo(ChronoUnit.MILLIS)
}
