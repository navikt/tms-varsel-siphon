@file:UseSerializers(ZonedDateTimeSerializer::class)
package no.nav.tms.varsel.siphon

import com.fasterxml.jackson.annotation.JsonValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
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
    Beskjed, Oppgave, Innboks;

    val lowercaseName = name.lowercase()

    @JsonValue
    fun toJson() = lowercaseName

    companion object {
        fun parse(string: String): VarselType {
            return values()
                .filter { it.lowercaseName == string.lowercase() }
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
)

@Serializable
enum class EksternStatus {
    Feilet, Info, Bestilt, Sendt, Ferdigstilt;

    val lowercaseName = name.lowercase()

    @JsonValue
    fun toJson() = lowercaseName
}

@Serializable
data class ArkivertVarsel(
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
