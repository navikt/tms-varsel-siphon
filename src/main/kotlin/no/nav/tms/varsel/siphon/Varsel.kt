@file:UseSerializers(ZonedDateTimeSerializer::class)
package no.nav.tms.varsel.siphon

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Serializable
data class Varsel(
    val type: VarselType,
    val fodselsnummer: String,
    val eventId: String,
    val aktiv: Boolean,
    val tekst: String,
    val link: String,
    val sikkerhetsnivaa: Int,
    val synligFremTil: ZonedDateTime?,
    val namespace: String,
    val appnavn: String,
    val forstBehandlet: ZonedDateTime,
    val eksternVarsling: Boolean,
    val prefererteKanaler: List<String>,
    val eksternVarslingStatus: EksternVarslingStatus?,
    val sistOppdatert: ZonedDateTime,
    val fristUtlopt: Boolean
)

@Serializable
enum class VarselType {
    beskjed, oppgave, innboks;

    companion object {
        fun parse(string: String): VarselType {
            return values()
                .filter { it.name == string.lowercase() }
                .firstOrNull() ?: throw RuntimeException("Could not parse varselType $string")
        }
    }
}

@Serializable
data class EksternVarslingStatus(
    val sendt: Boolean,
    val renotifikasjonSendt: Boolean,
    val kanaler: List<String>,
    val historikk: List<EksternVarslingHistorikkEntry>,
    val sistOppdatert: ZonedDateTime
)

@Serializable
data class EksternVarslingHistorikkEntry(
    val melding: String,
    val status: EksternStatus,
    val distribusjonsId: Long?,
    val kanal: String?,
    val renotifikasjon: Boolean?,
    val tidspunkt: ZonedDateTime
) {
    @JsonCreator
    constructor(
        melding: String,
        status: EksternStatus,
        distribusjonsId: Long?,
        kanal: String?,
        renotifikasjon: Boolean?,
        tidspunkt: String
    ): this(
        melding,
        status,
        distribusjonsId,
        kanal,
        renotifikasjon,
        tidspunkt.parseAsZonedDateTime()
    )
}

@Serializable
enum class EksternStatus {
    feilet, info, bestilt, sendt, ferdigstilt;
}

@Serializable
data class ArkivertVarsel(
    val type: VarselType,
    val eventId: String,
    val fodselsnummer: String,
    val tekst: String,
    val link: String,
    val sikkerhetsnivaa: Int,
    val aktiv: Boolean,
    val produsentApp: String,
    val eksternVarslingSendt: Boolean,
    val eksternVarslingKanaler: List<String>,
    val forstBehandlet: ZonedDateTime,
    val arkivert: ZonedDateTime,
    val fristUtlopt: Boolean
)
