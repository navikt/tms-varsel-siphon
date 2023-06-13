package no.nav.tms.varsel.siphon

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.ktor.util.*
import no.nav.tms.token.support.azure.validation.mock.installAzureAuthMock
import no.nav.tms.varsel.siphon.LocalDateTimeHelper.nowAtUtc
import no.nav.tms.varsel.siphon.VarselType.*
import no.nav.tms.varsel.siphon.ZonedDateTimeHelper.nowAtUtcZ
import no.nav.tms.varsel.siphon.database.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZonedDateTime

internal class VarselApiTest {

    private val database = LocalPostgresDatabase.cleanDb()
    private val repository = VarselRepository(database)

    @AfterAll
    fun cleanUp() {
        LocalPostgresDatabase.deleteFromTables(database)
    }

    val beskjedOldest = dbBeskjed(forstbehandlet = nowAtUtc().minusDays(10))
    val beskjedOld = dbBeskjed(forstbehandlet = nowAtUtc().minusDays(5))
    val beskjedNew = dbBeskjed(forstbehandlet = nowAtUtc())
    val eksternVarslingBeskjed = dbEksternVarslingStatus(beskjed, beskjedNew.eventId)
    val arkivertBeskjedOld = dbArkivVarsel(beskjed, arkivert = nowAtUtc().minusDays(50))
    val arkivertBeskjedOlder = dbArkivVarsel(beskjed, arkivert = nowAtUtc().minusDays(100))

    val oppgaveOldest = dbOppgave(forstbehandlet = nowAtUtc().minusDays(10))
    val oppgaveOld = dbOppgave(forstbehandlet = nowAtUtc().minusDays(5))
    val oppgaveNew = dbOppgave(forstbehandlet = nowAtUtc())
    val eksternVarslingOppgave = dbEksternVarslingStatus(oppgave, oppgaveNew.eventId)
    val arkivertOppgaveOlder = dbArkivVarsel(oppgave, arkivert = nowAtUtc().minusDays(100))
    val arkivertOppgaveOld = dbArkivVarsel(oppgave, arkivert = nowAtUtc().minusDays(50))

    val innboksOldest = dbInnboks(forstbehandlet = nowAtUtc().minusDays(10))
    val innboksOld = dbInnboks(forstbehandlet = nowAtUtc().minusDays(5))
    val innboksNew = dbInnboks(forstbehandlet = nowAtUtc())
    val eksternVarslingInnboks = dbEksternVarslingStatus(innboks, innboksNew.eventId)
    val arkivertInnboksOlder = dbArkivVarsel(innboks, arkivert = nowAtUtc().minusDays(100))
    val arkivertInnboksOld = dbArkivVarsel(innboks, arkivert = nowAtUtc().minusDays(50))

    @BeforeAll
    fun setup() {
        insertData(
            beskjedOldest, beskjedOld, beskjedNew, eksternVarslingBeskjed, arkivertBeskjedOld, arkivertBeskjedOlder,
            oppgaveOldest, oppgaveOld, oppgaveNew, eksternVarslingOppgave, arkivertOppgaveOld, arkivertOppgaveOlder,
            innboksOldest, innboksOld, innboksNew, eksternVarslingInnboks, arkivertInnboksOld, arkivertInnboksOlder
        )
    }

    @Test
    fun `svarer med beskjeder mellom datoer`() = testVarselApi { client ->

        val response = client.getVarsler(
            type = beskjed,
            fromDate = nowAtUtcZ().minusDays(6),
            toDate = nowAtUtcZ().plusDays(1),
            max = 5
        )

        val beskjedList: List<Varsel> = response.body()

        beskjedList.size shouldBe 2
        beskjedList.map { it.eventId }.let { eventIds ->
           eventIds shouldContain beskjedOld.eventId
           eventIds shouldContain beskjedNew.eventId
        }
    }

    @Test
    fun `svarer med oppgaver mellom datoer`() = testVarselApi { client ->

        val response = client.getVarsler(
            type = oppgave,
            fromDate = nowAtUtcZ().minusDays(6),
            toDate = nowAtUtcZ().plusDays(1),
            max = 5
        )

        val oppgaveList: List<Varsel> = response.body()

        oppgaveList.size shouldBe 2
        oppgaveList.map { it.eventId }.let { eventIds ->
           eventIds shouldContain oppgaveOld.eventId
           eventIds shouldContain oppgaveNew.eventId
        }
    }

    @Test
    fun `svarer med innbokser mellom datoer`() = testVarselApi { client ->

        val response = client.getVarsler(
            type = innboks,
            fromDate = nowAtUtcZ().minusDays(6),
            toDate = nowAtUtcZ().plusDays(1),
            max = 5
        )

        val innboksList: List<Varsel> = response.body()

        innboksList.size shouldBe 2
        innboksList.map { it.eventId }.let { eventIds ->
           eventIds shouldContain innboksOld.eventId
           eventIds shouldContain innboksNew.eventId
        }
    }

    @Test
    fun `svarer med riktig beskjed-data`() = testVarselApi { client ->
        val responseBeskjed = client.getVarsler(
            type = beskjed,
            fromDate = nowAtUtcZ().minusDays(1),
            toDate = nowAtUtcZ().plusDays(1),
            max = 5
        ).body<List<Varsel>>().first()

        responseBeskjed.type shouldBe beskjed
        responseBeskjed.fodselsnummer shouldBe beskjedNew.fodselsnummer
        responseBeskjed.eventId shouldBe beskjedNew.eventId
        responseBeskjed.aktiv shouldBe beskjedNew.aktiv
        responseBeskjed.tekst shouldBe beskjedNew.tekst
        responseBeskjed.link shouldBe beskjedNew.link
        responseBeskjed.sikkerhetsnivaa shouldBe beskjedNew.sikkerhetsnivaa
        responseBeskjed.synligFremTil shouldEqual beskjedNew.synligFremTil
        responseBeskjed.namespace shouldBe beskjedNew.namespace
        responseBeskjed.appnavn shouldBe beskjedNew.appnavn
        responseBeskjed.forstBehandlet shouldEqual beskjedNew.forstbehandlet
        responseBeskjed.eksternVarsling shouldBe beskjedNew.eksternVarsling
        responseBeskjed.prefererteKanaler.joinToString(",") shouldBe beskjedNew.preferertekanaler
        responseBeskjed.sistOppdatert shouldEqual beskjedNew.sistOppdatert
        responseBeskjed.fristUtlopt shouldBe beskjedNew.fristUtlopt

        val eksternVarslingStatus = responseBeskjed.eksternVarslingStatus

        eksternVarslingStatus.shouldNotBeNull()
        eksternVarslingStatus.sendt shouldBe eksternVarslingBeskjed.eksternVarslingSendt
        eksternVarslingStatus.renotifikasjonSendt shouldBe eksternVarslingBeskjed.renotifikasjonSendt
        eksternVarslingStatus.kanaler.joinToString(",") shouldBe eksternVarslingBeskjed.kanaler
        eksternVarslingStatus.historikk shouldBe eksternVarslingBeskjed.historikk
        eksternVarslingStatus.sistOppdatert shouldEqual eksternVarslingBeskjed.sistOppdatert
    }

    @Test
    fun `svarer med riktig oppgave-data`() = testVarselApi { client ->
        val responseOppgave = client.getVarsler(
            type = oppgave,
            fromDate = nowAtUtcZ().minusDays(1),
            toDate = nowAtUtcZ().plusDays(1),
            max = 5
        ).body<List<Varsel>>().first()

        responseOppgave.type shouldBe oppgave
        responseOppgave.fodselsnummer shouldBe oppgaveNew.fodselsnummer
        responseOppgave.eventId shouldBe oppgaveNew.eventId
        responseOppgave.aktiv shouldBe oppgaveNew.aktiv
        responseOppgave.tekst shouldBe oppgaveNew.tekst
        responseOppgave.link shouldBe oppgaveNew.link
        responseOppgave.sikkerhetsnivaa shouldBe oppgaveNew.sikkerhetsnivaa
        responseOppgave.synligFremTil shouldEqual oppgaveNew.synligFremTil
        responseOppgave.namespace shouldBe oppgaveNew.namespace
        responseOppgave.appnavn shouldBe oppgaveNew.appnavn
        responseOppgave.forstBehandlet shouldEqual oppgaveNew.forstbehandlet
        responseOppgave.eksternVarsling shouldBe oppgaveNew.eksternVarsling
        responseOppgave.prefererteKanaler.joinToString(",") shouldBe oppgaveNew.preferertekanaler
        responseOppgave.sistOppdatert shouldEqual oppgaveNew.sistOppdatert
        responseOppgave.fristUtlopt shouldBe oppgaveNew.fristUtlopt

        val eksternVarslingStatus = responseOppgave.eksternVarslingStatus

        eksternVarslingStatus.shouldNotBeNull()
        eksternVarslingStatus.sendt shouldBe eksternVarslingOppgave.eksternVarslingSendt
        eksternVarslingStatus.renotifikasjonSendt shouldBe eksternVarslingOppgave.renotifikasjonSendt
        eksternVarslingStatus.kanaler.joinToString(",") shouldBe eksternVarslingOppgave.kanaler
        eksternVarslingStatus.historikk shouldBe eksternVarslingOppgave.historikk
        eksternVarslingStatus.sistOppdatert shouldEqual eksternVarslingOppgave.sistOppdatert
    }

    @Test
    fun `svarer med riktig innboks-data`() = testVarselApi { client ->
        val responseInnboks = client.getVarsler(
            type = innboks,
            fromDate = nowAtUtcZ().minusDays(1),
            toDate = nowAtUtcZ().plusDays(1),
            max = 5
        ).body<List<Varsel>>().first()

        responseInnboks.type shouldBe innboks
        responseInnboks.fodselsnummer shouldBe innboksNew.fodselsnummer
        responseInnboks.eventId shouldBe innboksNew.eventId
        responseInnboks.aktiv shouldBe innboksNew.aktiv
        responseInnboks.tekst shouldBe innboksNew.tekst
        responseInnboks.link shouldBe innboksNew.link
        responseInnboks.sikkerhetsnivaa shouldBe innboksNew.sikkerhetsnivaa
        responseInnboks.synligFremTil shouldEqual null
        responseInnboks.namespace shouldBe innboksNew.namespace
        responseInnboks.appnavn shouldBe innboksNew.appnavn
        responseInnboks.forstBehandlet shouldEqual innboksNew.forstbehandlet
        responseInnboks.eksternVarsling shouldBe innboksNew.eksternVarsling
        responseInnboks.prefererteKanaler.joinToString(",") shouldBe innboksNew.preferertekanaler
        responseInnboks.sistOppdatert shouldEqual innboksNew.sistOppdatert
        responseInnboks.fristUtlopt shouldBe innboksNew.fristUtlopt

        val eksternVarslingStatus = responseInnboks.eksternVarslingStatus

        eksternVarslingStatus.shouldNotBeNull()
        eksternVarslingStatus.sendt shouldBe eksternVarslingInnboks.eksternVarslingSendt
        eksternVarslingStatus.renotifikasjonSendt shouldBe eksternVarslingInnboks.renotifikasjonSendt
        eksternVarslingStatus.kanaler.joinToString(",") shouldBe eksternVarslingInnboks.kanaler
        eksternVarslingStatus.historikk shouldBe eksternVarslingInnboks.historikk
        eksternVarslingStatus.sistOppdatert shouldEqual eksternVarslingInnboks.sistOppdatert
    }

    @Test
    fun `svarer med arkiverte beskjeder`() = testVarselApi { client ->
        val arkiverteBeskjeder = client.getArkiverteVarsler(
            type = beskjed,
            fromDate = nowAtUtcZ().minusDays(365),
            toDate = nowAtUtcZ().minusDays(40),
            max = 5
        ).body<List<ArkivertVarsel>>()

        arkiverteBeskjeder.size shouldBe 2
    }

    @Test
    fun `svarer med arkiverte oppgaver`() = testVarselApi { client ->
        val arkiverteOppgaver = client.getArkiverteVarsler(
            type = oppgave,
            fromDate = nowAtUtcZ().minusDays(365),
            toDate = nowAtUtcZ().minusDays(40),
            max = 5
        ).body<List<ArkivertVarsel>>()

        arkiverteOppgaver.size shouldBe 2
    }

    @Test
    fun `svarer med arkiverte innbokser`() = testVarselApi { client ->
        val arkiverteInnbokser = client.getArkiverteVarsler(
            type = innboks,
            fromDate = nowAtUtcZ().minusDays(365),
            toDate = nowAtUtcZ().minusDays(40),
            max = 5
        ).body<List<ArkivertVarsel>>()

        arkiverteInnbokser.size shouldBe 2
    }

    @Test
    fun `svarer med riktig arkivert beskjed-data`() = testVarselApi { client ->
        val arkivertBeskjed = client.getArkiverteVarsler(
            type = beskjed,
            fromDate = nowAtUtcZ().minusDays(365),
            toDate = nowAtUtcZ().minusDays(60),
            max = 5
        ).body<List<ArkivertVarsel>>().first()

        arkivertBeskjed.eventId shouldBe arkivertBeskjedOlder.eventId
        arkivertBeskjed.fodselsnummer shouldBe arkivertBeskjedOlder.fodselsnummer
        arkivertBeskjed.tekst shouldBe arkivertBeskjedOlder.tekst
        arkivertBeskjed.link shouldBe arkivertBeskjedOlder.link
        arkivertBeskjed.sikkerhetsnivaa shouldBe arkivertBeskjedOlder.sikkerhetsnivaa
        arkivertBeskjed.aktiv shouldBe arkivertBeskjedOlder.aktiv
        arkivertBeskjed.produsentApp shouldBe arkivertBeskjedOlder.produsentapp
        arkivertBeskjed.eksternVarslingSendt shouldBe arkivertBeskjedOlder.eksternvarslingsendt
        arkivertBeskjed.eksternVarslingKanaler.joinToString(",") shouldBe arkivertBeskjedOlder.eksternvarslingkanaler
        arkivertBeskjed.forstBehandlet shouldEqual arkivertBeskjedOlder.forstbehandlet
        arkivertBeskjed.arkivert shouldEqual arkivertBeskjedOlder.arkivert
        arkivertBeskjed.fristUtlopt shouldBe (arkivertBeskjedOlder.fristUtlopt ?: false)
    }

    @Test
    fun `svarer med riktig arkivert oppgave-data`() = testVarselApi { client ->
        val arkivertOppgave = client.getArkiverteVarsler(
            type = oppgave,
            fromDate = nowAtUtcZ().minusDays(365),
            toDate = nowAtUtcZ().minusDays(60),
            max = 5
        ).body<List<ArkivertVarsel>>().first()

        arkivertOppgave.eventId shouldBe arkivertOppgaveOlder.eventId
        arkivertOppgave.fodselsnummer shouldBe arkivertOppgaveOlder.fodselsnummer
        arkivertOppgave.tekst shouldBe arkivertOppgaveOlder.tekst
        arkivertOppgave.link shouldBe arkivertOppgaveOlder.link
        arkivertOppgave.sikkerhetsnivaa shouldBe arkivertOppgaveOlder.sikkerhetsnivaa
        arkivertOppgave.aktiv shouldBe arkivertOppgaveOlder.aktiv
        arkivertOppgave.produsentApp shouldBe arkivertOppgaveOlder.produsentapp
        arkivertOppgave.eksternVarslingSendt shouldBe arkivertOppgaveOlder.eksternvarslingsendt
        arkivertOppgave.eksternVarslingKanaler.joinToString(",") shouldBe arkivertOppgaveOlder.eksternvarslingkanaler
        arkivertOppgave.forstBehandlet shouldEqual arkivertOppgaveOlder.forstbehandlet
        arkivertOppgave.arkivert shouldEqual arkivertOppgaveOlder.arkivert
        arkivertOppgave.fristUtlopt shouldBe (arkivertOppgaveOlder.fristUtlopt ?: false)
    }

    @Test
    fun `svarer med riktig arkivert innboks-data`() = testVarselApi { client ->
        val arkivertInnboks = client.getArkiverteVarsler(
            type = innboks,
            fromDate = nowAtUtcZ().minusDays(365),
            toDate = nowAtUtcZ().minusDays(60),
            max = 5
        ).body<List<ArkivertVarsel>>().first()

        arkivertInnboks.eventId shouldBe arkivertInnboksOlder.eventId
        arkivertInnboks.fodselsnummer shouldBe arkivertInnboksOlder.fodselsnummer
        arkivertInnboks.tekst shouldBe arkivertInnboksOlder.tekst
        arkivertInnboks.link shouldBe arkivertInnboksOlder.link
        arkivertInnboks.sikkerhetsnivaa shouldBe arkivertInnboksOlder.sikkerhetsnivaa
        arkivertInnboks.aktiv shouldBe arkivertInnboksOlder.aktiv
        arkivertInnboks.produsentApp shouldBe arkivertInnboksOlder.produsentapp
        arkivertInnboks.eksternVarslingSendt shouldBe arkivertInnboksOlder.eksternvarslingsendt
        arkivertInnboks.eksternVarslingKanaler.joinToString(",") shouldBe arkivertInnboksOlder.eksternvarslingkanaler
        arkivertInnboks.forstBehandlet shouldEqual arkivertInnboksOlder.forstbehandlet
        arkivertInnboks.arkivert shouldEqual arkivertInnboksOlder.arkivert
        arkivertInnboks.fristUtlopt shouldBe (arkivertInnboksOlder.fristUtlopt ?: false)
    }

    @Test
    fun `filtrerer beskjeder etter paramatere`() = testVarselApi { client ->
        val alleBeskjeder = client.getVarsler(
            type = beskjed,
            fromDate = nowAtUtcZ().minusDays(20),
            toDate = nowAtUtcZ().plusDays(1),
            max = 5
        ).body<List<Varsel>>()

        val gamleBeskjeder = client.getVarsler(
            type = beskjed,
            fromDate = nowAtUtcZ().minusDays(20),
            toDate = nowAtUtcZ().minusDays(1),
            max = 5
        ).body<List<Varsel>>()

        val fremtidigeBeskjeder = client.getVarsler(
            type = beskjed,
            fromDate = nowAtUtcZ().plusDays(1),
            toDate = nowAtUtcZ().plusDays(10),
            max = 5
        ).body<List<Varsel>>()

        val maxEnBeskjed = client.getVarsler(
            type = beskjed,
            fromDate = nowAtUtcZ().minusDays(20),
            toDate = nowAtUtcZ().plusDays(1),
            max = 1
        ).body<List<Varsel>>()

        alleBeskjeder.size shouldBe 3

        gamleBeskjeder.size shouldBe 2
        gamleBeskjeder.map { it.eventId }.let {
            it shouldContain beskjedOld.eventId
            it shouldContain beskjedOldest.eventId
        }

        fremtidigeBeskjeder.size shouldBe 0

        maxEnBeskjed.size shouldBe 1
        maxEnBeskjed.map { it.eventId }.let {
            it shouldContain beskjedOldest.eventId
        }
    }

    @Test
    fun `filtrerer oppgaver etter paramatere`() = testVarselApi { client ->
        val alleOppgaver = client.getVarsler(
            type = oppgave,
            fromDate = nowAtUtcZ().minusDays(20),
            toDate = nowAtUtcZ().plusDays(1),
            max = 5
        ).body<List<Varsel>>()

        val gamleOppgaver = client.getVarsler(
            type = oppgave,
            fromDate = nowAtUtcZ().minusDays(20),
            toDate = nowAtUtcZ().minusDays(1),
            max = 5
        ).body<List<Varsel>>()

        val fremtidigeOppgaver = client.getVarsler(
            type = oppgave,
            fromDate = nowAtUtcZ().plusDays(1),
            toDate = nowAtUtcZ().plusDays(10),
            max = 5
        ).body<List<Varsel>>()

        val maxEnOppgave = client.getVarsler(
            type = oppgave,
            fromDate = nowAtUtcZ().minusDays(20),
            toDate = nowAtUtcZ().plusDays(1),
            max = 1
        ).body<List<Varsel>>()

        alleOppgaver.size shouldBe 3

        gamleOppgaver.size shouldBe 2
        gamleOppgaver.map { it.eventId }.let {
            it shouldContain oppgaveOld.eventId
            it shouldContain oppgaveOldest.eventId
        }

        fremtidigeOppgaver.size shouldBe 0

        maxEnOppgave.size shouldBe 1
        maxEnOppgave.map { it.eventId }.let {
            it shouldContain oppgaveOldest.eventId
        }
    }

    @Test
    fun `filtrerer innbokser etter paramatere`() = testVarselApi { client ->
        val alleInnbokser = client.getVarsler(
            type = innboks,
            fromDate = nowAtUtcZ().minusDays(20),
            toDate = nowAtUtcZ().plusDays(1),
            max = 5
        ).body<List<Varsel>>()

        val gamleInnbokser = client.getVarsler(
            type = innboks,
            fromDate = nowAtUtcZ().minusDays(20),
            toDate = nowAtUtcZ().minusDays(1),
            max = 5
        ).body<List<Varsel>>()

        val fremtidigeInnbokser = client.getVarsler(
            type = innboks,
            fromDate = nowAtUtcZ().plusDays(1),
            toDate = nowAtUtcZ().plusDays(10),
            max = 5
        ).body<List<Varsel>>()

        val maxEnInnboks = client.getVarsler(
            type = innboks,
            fromDate = nowAtUtcZ().minusDays(20),
            toDate = nowAtUtcZ().plusDays(1),
            max = 1
        ).body<List<Varsel>>()

        alleInnbokser.size shouldBe 3

        gamleInnbokser.size shouldBe 2
        gamleInnbokser.map { it.eventId }.let {
            it shouldContain innboksOld.eventId
            it shouldContain innboksOldest.eventId
        }

        fremtidigeInnbokser.size shouldBe 0

        maxEnInnboks.size shouldBe 1
        maxEnInnboks.map { it.eventId }.let {
            it shouldContain innboksOldest.eventId
        }
    }

    @Test
    fun `filtrerer arkiverte beskjeder etter paramatere`() = testVarselApi { client ->
        val alleArkiverteBeskjeder = client.getArkiverteVarsler(
            type = beskjed,
            fromDate = nowAtUtcZ().minusDays(500),
            toDate = nowAtUtcZ(),
            max = 5
        ).body<List<ArkivertVarsel>>()

        val gamleArkiverteBeskjeder = client.getArkiverteVarsler(
            type = beskjed,
            fromDate = nowAtUtcZ().minusDays(500),
            toDate = nowAtUtcZ().minusDays(100),
            max = 5
        ).body<List<ArkivertVarsel>>()

        val nyeArkiverteBeskjeder = client.getArkiverteVarsler(
            type = beskjed,
            fromDate = nowAtUtcZ().minusDays(10),
            toDate = nowAtUtcZ(),
            max = 5
        ).body<List<ArkivertVarsel>>()

        val maxEnArkivertBeskjed = client.getArkiverteVarsler(
            type = beskjed,
            fromDate = nowAtUtcZ().minusDays(500),
            toDate = nowAtUtcZ(),
            max = 1
        ).body<List<ArkivertVarsel>>()

        alleArkiverteBeskjeder.size shouldBe 2

        gamleArkiverteBeskjeder.size shouldBe 1
        gamleArkiverteBeskjeder.map { it.eventId }.let {
            it shouldContain arkivertBeskjedOlder.eventId
        }

        nyeArkiverteBeskjeder.size shouldBe 0

        maxEnArkivertBeskjed.size shouldBe 1
        maxEnArkivertBeskjed.map { it.eventId }.let {
            it shouldContain arkivertBeskjedOlder.eventId
        }
    }

    @Test
    fun `filtrerer arkiverte oppgaver etter paramatere`() = testVarselApi { client ->
        val alleArkiverteOppgaver = client.getArkiverteVarsler(
            type = oppgave,
            fromDate = nowAtUtcZ().minusDays(500),
            toDate = nowAtUtcZ(),
            max = 5
        ).body<List<ArkivertVarsel>>()

        val gamleArkiverteOppgaver = client.getArkiverteVarsler(
            type = oppgave,
            fromDate = nowAtUtcZ().minusDays(500),
            toDate = nowAtUtcZ().minusDays(100),
            max = 5
        ).body<List<ArkivertVarsel>>()

        val nyeArkiverteOppgaver = client.getArkiverteVarsler(
            type = oppgave,
            fromDate = nowAtUtcZ().minusDays(10),
            toDate = nowAtUtcZ(),
            max = 5
        ).body<List<ArkivertVarsel>>()

        val maxEnArkivertOppgave = client.getArkiverteVarsler(
            type = oppgave,
            fromDate = nowAtUtcZ().minusDays(500),
            toDate = nowAtUtcZ(),
            max = 1
        ).body<List<ArkivertVarsel>>()

        alleArkiverteOppgaver.size shouldBe 2

        gamleArkiverteOppgaver.size shouldBe 1
        gamleArkiverteOppgaver.map { it.eventId }.let {
            it shouldContain arkivertOppgaveOlder.eventId
        }

        nyeArkiverteOppgaver.size shouldBe 0

        maxEnArkivertOppgave.size shouldBe 1
        maxEnArkivertOppgave.map { it.eventId }.let {
            it shouldContain arkivertOppgaveOlder.eventId
        }
    }

    @Test
    fun `filtrerer arkiverte innbokser etter paramatere`() = testVarselApi { client ->
        val alleArkiverteInnbokser = client.getArkiverteVarsler(
            type = innboks,
            fromDate = nowAtUtcZ().minusDays(500),
            toDate = nowAtUtcZ(),
            max = 5
        ).body<List<ArkivertVarsel>>()

        val gamleArkiverteInnbokser = client.getArkiverteVarsler(
            type = innboks,
            fromDate = nowAtUtcZ().minusDays(500),
            toDate = nowAtUtcZ().minusDays(100),
            max = 5
        ).body<List<ArkivertVarsel>>()

        val nyeArkiverteInnbokser = client.getArkiverteVarsler(
            type = innboks,
            fromDate = nowAtUtcZ().minusDays(10),
            toDate = nowAtUtcZ(),
            max = 5
        ).body<List<ArkivertVarsel>>()

        val maxEnArkivertInnboks = client.getArkiverteVarsler(
            type = innboks,
            fromDate = nowAtUtcZ().minusDays(500),
            toDate = nowAtUtcZ(),
            max = 1
        ).body<List<ArkivertVarsel>>()

        alleArkiverteInnbokser.size shouldBe 2

        gamleArkiverteInnbokser.size shouldBe 1
        gamleArkiverteInnbokser.map { it.eventId }.let {
            it shouldContain arkivertInnboksOlder.eventId
        }

        nyeArkiverteInnbokser.size shouldBe 0

        maxEnArkivertInnboks.size shouldBe 1
        maxEnArkivertInnboks.map { it.eventId }.let {
            it shouldContain arkivertInnboksOlder.eventId
        }
    }

    @KtorDsl
    private fun testVarselApi(block: suspend ApplicationTestBuilder.(HttpClient) -> Unit) = testApplication {

        application {
            configureApi(
                repository,
                installAuthenticatorsFunction = {
                    installAzureAuthMock {
                        setAsDefault = true
                        alwaysAuthenticated = true
                    }
                }
            )
        }

        this.block(
            client.config {
                install(ContentNegotiation) {
                    json()
                }
            }
        )
    }

    private fun insertData(vararg dataList: Any) {
        dataList.forEach { data ->
            when(data) {
                is DatabaseBeskjed -> database.insertBeskjed(data)
                is DatabaseOppgave -> database.insertOppgave(data)
                is DatabaseInnboks -> database.insertInnboks(data)
                is DatabaseEksternVarslingStatus -> database.insertEksternVarslingStatus(data)
                is DatabaseArkivVarsel -> database.insertArkivVarsel(data)
                else -> throw RuntimeException("Invalid type ${data::class.simpleName}")
            }
        }
    }

    private suspend fun HttpClient.getVarsler(
        type: VarselType? = null,
        fromDate: ZonedDateTime? = null,
        toDate: ZonedDateTime? = null,
        max: Int? = null
    ): HttpResponse {
        return get("/varsler") {
            url {
                if (type != null) parameters.append("type", type.name)
                if (fromDate != null) parameters.append("fraDato", fromDate.toString())
                if (toDate != null) parameters.append("tilDato", toDate.toString())
                if (max != null) parameters.append("max", max.toString())
            }
        }
    }

    private suspend fun HttpClient.getArkiverteVarsler(
        type: VarselType? = null,
        fromDate: ZonedDateTime? = null,
        toDate: ZonedDateTime? = null,
        max: Int? = null
    ): HttpResponse {
        return get("/arkiv/varsler") {
            url {
                if (type != null) parameters.append("type", type.name)
                if (fromDate != null) parameters.append("fraDato", fromDate.toString())
                if (toDate != null) parameters.append("tilDato", toDate.toString())
                if (max != null) parameters.append("max", max.toString())
            }
        }
    }

    private infix fun ZonedDateTime?.shouldEqual(other: LocalDateTime?) {
        this?.toLocalDateTime() shouldBe other
    }
}
