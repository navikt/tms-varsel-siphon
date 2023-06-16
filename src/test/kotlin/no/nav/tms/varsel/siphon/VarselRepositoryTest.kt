package no.nav.tms.varsel.siphon

import io.kotest.matchers.shouldBe
import no.nav.tms.varsel.siphon.ZonedDateTimeHelper.nowAtUtcZ
import no.nav.tms.varsel.siphon.database.LocalPostgresDatabase
import no.nav.tms.varsel.siphon.database.insertBeskjed
import no.nav.tms.varsel.siphon.database.insertEksternVarslingStatus
import no.nav.tms.varsel.siphon.database.insertEksternVarslingStatusWithLocalDateTime
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime

internal class VarselRepositoryTest {

    private val database = LocalPostgresDatabase.cleanDb()
    private val repository = VarselRepository(database)

    @AfterAll
    fun cleanUp() {
        LocalPostgresDatabase.deleteFromTables(database)
    }

    val historikk = listOf(
        EksternVarslingHistorikkEntry(
            melding = "Oversendt",
            status = EksternStatus.bestilt,
            distribusjonsId = null,
            kanal = null,
            renotifikasjon = null,
            tidspunkt = nowAtUtcZ()
        )
    )

    val beskjed1 = dbBeskjed(forstbehandlet = LocalDateTimeHelper.nowAtUtc())
    val eksternVarslingBeskjed1 = dbEksternVarslingStatus(VarselType.beskjed, beskjed1.eventId, historikk = historikk)

    val beskjed2 = dbBeskjed(forstbehandlet = LocalDateTimeHelper.nowAtUtc())
    val eksternVarslingBeskjed2 = dbEksternVarslingStatus(VarselType.beskjed, beskjed2.eventId, historikk = historikk)

    @AfterEach
    fun setup() {
        LocalPostgresDatabase.deleteFromTables(database)
    }

    @Test
    fun `godtar at tidspunkt for ekstern varsling historikk er lagret som LocalDateTime`() {
        database.insertBeskjed(beskjed1)
        database.insertBeskjed(beskjed2)
        database.insertEksternVarslingStatus(eksternVarslingBeskjed1)
        database.insertEksternVarslingStatusWithLocalDateTime(eksternVarslingBeskjed2)

        val varsler = repository.fetchVarselList(
            VarselType.beskjed,
            fromDate = nowAtUtcZ().minusDays(1),
            toDate = nowAtUtcZ().plusDays(1),
            max = 5
        )

        varsler.size shouldBe 2

        val varsel1 = varsler.find { it.eventId == beskjed1.eventId }
        val varsel2 = varsler.find { it.eventId == beskjed2.eventId }

        varsel1!!.evHistorikkEntry().tidspunkt.toInstant() shouldBe varsel2!!.evHistorikkEntry().tidspunkt.toInstant()
    }

    @Test
    fun `behandler timestamp uten tidssone som utc`() {
        val date = LocalDate.parse("2023-06-15")

        createBeskjedAtHours(date, 0..5).forEach {
            database.insertBeskjed(it)
        }

        val fromTime = ZonedDateTime.parse("2023-06-15T00:00:00Z")
        val toTime = ZonedDateTime.parse("2023-06-15T05:00:00Z")

        val varsler = repository.fetchVarselList(
            VarselType.beskjed,
            fromDate = fromTime,
            toDate = toTime,
            max = 10
        )

        varsler.size shouldBe 6
    }

    @Test
    fun `konverter input til utc for query`() {
        val date = LocalDate.parse("2023-06-15")

        createBeskjedAtHours(date, 0..5).forEach {
            database.insertBeskjed(it)
        }


        val fromTime = ZonedDateTime.parse("2023-06-15T06:00:00+06:00")
        val toTime = ZonedDateTime.parse("2023-06-15T11:00:00+06:00")

        val varsler = repository.fetchVarselList(
            VarselType.beskjed,
            fromDate = fromTime,
            toDate = toTime,
            max = 10
        )

        varsler.size shouldBe 6
    }

    private fun createBeskjedAtHours(date: LocalDate, hours: IntRange): List<DatabaseBeskjed> {

        return hours.map {
            date.atTime(it, 0)
        }.map {
            dbBeskjed(forstbehandlet = it)
        }
    }

    private fun Varsel.evHistorikkEntry() = eksternVarslingStatus!!.historikk.first()
}
