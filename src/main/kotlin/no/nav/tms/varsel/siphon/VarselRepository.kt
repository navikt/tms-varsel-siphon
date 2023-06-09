package no.nav.tms.varsel.siphon

import kotliquery.Row
import kotliquery.queryOf
import no.nav.tms.varsel.siphon.database.Database
import no.nav.tms.varsel.siphon.database.defaultObjectMapper
import no.nav.tms.varsel.siphon.database.jsonOrNull
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class VarselRepository(private val database: Database) {

    private val objectMapper = defaultObjectMapper()

    fun fetchVarselList(type: VarselType, fromDate: ZonedDateTime, toDate: ZonedDateTime, max: Int): List<Varsel> {
        return database.list {
            queryOf(
                fetchVarselQuery(type),
                mapOf(
                    "start" to fromDate.atUtc(),
                    "end" to toDate.atUtc(),
                    "max" to max
                )
            )
                .map(toVarsel(type))
                .asList
        }
    }

    fun fetchArkivertVarselList(type: VarselType, fromDate: ZonedDateTime, toDate: ZonedDateTime, max: Int): List<ArkivertVarsel> {
        return database.list {
            queryOf(
                fetchArchivedVarselQuery(type),
                mapOf(
                    "start" to fromDate.atUtc(),
                    "end" to toDate.atUtc(),
                    "max" to max
                )
            )
                .map(toArchivedVarsel(type))
                .asList
        }
    }

    private fun toVarsel(type: VarselType): (Row) -> Varsel = {
        Varsel(
            type = type,
            fodselsnummer = it.string("fodselsnummer"),
            eventId = it.string("eventId"),
            aktiv = it.boolean("aktiv"),
            tekst = it.string("tekst"),
            link = it.string("link"),
            sikkerhetsnivaa = it.int("sikkerhetsnivaa"),
            forstBehandlet = it.utcZonedDateTime("forstBehandlet"),
            synligFremTil = it.utcZonedDateTimeOrNull("synligFremTil"),
            sistOppdatert = it.utcZonedDateTime("sistOppdatert"),
            fristUtlopt = it.boolean("frist_utløpt"),
            prefererteKanaler = it.list("prefererteKanaler"),
            appnavn = it.string("appnavn"),
            namespace = it.string("namespace"),
            eksternVarsling = it.boolean("eksternVarsling"),
            eksternVarslingStatus = it.eksternVarslingStatus()
        )
    }

    private fun Row.eksternVarslingStatus(): EksternVarslingStatus? {
        return if (stringOrNull("ev_eventId") == null) {
            null
        } else {
            EksternVarslingStatus(
                sendt = boolean("ev_eksternvarslingsendt"),
                renotifikasjonSendt = boolean("ev_renotifikasjonSendt"),
                kanaler = list("ev_kanaler"),
                historikk = jsonOrNull("ev_historikk", objectMapper) ?: emptyList(),
                sistOppdatert = zonedDateTime("ev_sistOppdatert")
            )
        }
    }

    private fun toArchivedVarsel(type: VarselType): (Row) -> ArkivertVarsel = {
        ArkivertVarsel(
            type = type,
            eventId = it.string("eventId"),
            fodselsnummer = it.string("fodselsnummer"),
            tekst = it.string("tekst"),
            link = it.string("link"),
            sikkerhetsnivaa = it.int("sikkerhetsnivaa"),
            aktiv = it.boolean("aktiv"),
            produsentApp = it.string("produsentApp"),
            eksternVarslingSendt = it.boolean("eksternVarslingSendt"),
            eksternVarslingKanaler = it.list("eksternVarslingKanaler"),
            forstBehandlet = it.utcZonedDateTime("forstBehandlet"),
            arkivert = it.utcZonedDateTime("arkivert"),
            fristUtlopt = it.boolean("frist_utløpt")
        )
    }

    private fun fetchVarselQuery(varselType: VarselType) = """
        select
          v.fodselsnummer,
          v.eventid,
          v.tekst,
          v.link,
          v.sikkerhetsnivaa,
          v.sistoppdatert,
          v.aktiv,
          ${if(varselType == VarselType.innboks) "null as synligfremtil" else "v.synligfremtil"},
          v.eksternvarsling,
          v.preferertekanaler,
          v.namespace,
          v.appnavn,
          v.forstbehandlet,
          v.frist_utløpt,
          ${if(varselType == VarselType.innboks) "false as frist_utløpt" else "v.frist_utløpt"},
          dsv.eventId as ev_eventId,
          dsv.sistoppdatert as ev_sistoppdatert,
          dsv.kanaler as ev_kanaler,
          dsv.eksternvarslingsendt as ev_eksternvarslingsendt,
          dsv.renotifikasjonsendt as ev_renotifikasjonsendt,
          dsv.historikk as ev_historikk
        from ${varselType.name} as v
          left join ekstern_varsling_status_${varselType.name} as dsv on v.eventId = dsv.eventId
          where forstbehandlet between :start and :end order by forstbehandlet limit :max
    """

    private fun fetchArchivedVarselQuery(varselType: VarselType) = """
        select
          eventid,
          fodselsnummer,
          tekst,
          link,
          sikkerhetsnivaa,
          aktiv,
          produsentapp,
          eksternvarslingsendt,
          eksternvarslingkanaler,
          forstbehandlet,
          arkivert,
          frist_utløpt
        from ${varselType.name}_arkiv 
          where arkivert between :start and :end order by arkivert limit :max
    """

    private fun ZonedDateTime.atUtc() = withZoneSameInstant(ZoneId.of("Z")).toLocalDateTime()

    private fun Row.utcZonedDateTime(label: String): ZonedDateTime {
        return utcZonedDateTimeOrNull(label)!!
    }

    private fun Row.utcZonedDateTimeOrNull(label: String): ZonedDateTime? {
        return sqlTimestampOrNull(label, Calendar.getInstance(TimeZone.getTimeZone("Z")))
            ?.let { ZonedDateTime.ofInstant(it.toInstant(), ZoneId.of("Z")) }
    }

    private fun Row.list(label: String, separator: String = ","): List<String> {
        val asString = stringOrNull(label)

        return if (asString.isNullOrEmpty()) {
            emptyList()
        } else {
            asString.split(separator)
        }
    }
}
