package no.nav.tms.varsel.siphon

import io.kotest.matchers.shouldBe
import no.nav.tms.varsel.siphon.ZonedDateTimeHelper.nowAtUtcZ
import no.nav.tms.varsel.siphon.database.LocalPostgresDatabase
import no.nav.tms.varsel.siphon.database.insertBeskjed
import no.nav.tms.varsel.siphon.database.insertEksternVarslingStatus
import no.nav.tms.varsel.siphon.database.insertEksternVarslingStatusWithLocalDateTime
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

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

    @BeforeAll
    fun setup() {
        database.insertBeskjed(beskjed1)
        database.insertBeskjed(beskjed2)
        database.insertEksternVarslingStatus(eksternVarslingBeskjed1)
        database.insertEksternVarslingStatusWithLocalDateTime(eksternVarslingBeskjed2)
    }

    @Test
    fun `godtar at tidspunkt for ekstern varsling historikk er lagret som LocalDateTime`() {
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

    private fun Varsel.evHistorikkEntry() = eksternVarslingStatus!!.historikk.first()
}
