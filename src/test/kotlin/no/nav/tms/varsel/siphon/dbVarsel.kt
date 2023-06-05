package no.nav.tms.varsel.siphon

import java.time.LocalDateTime

data class DatabaseBeskjed(
    val id: Long,
    val systembruker: String,
    val eventTidspunkt: LocalDateTime,
    val fodselsnummer: String,
    val eventId: String,
    val grupperingsId: String,
    val tekst: String,
    val link: String,
    val sikkerhetsnivaa: Int,
    val sistOppdatert: LocalDateTime,
    val aktiv: Boolean,
    val synligFremTil: LocalDateTime,
    val uid: String,
    val eksternVarsling: Boolean,
    val preferertekanaler: String,
    val namespace: String,
    val appnavn: String,
    val forstbehandlet: LocalDateTime,
    val fristUtlopt: Boolean?
)

data class DatabaseOppgave(
    val id: Long,
    val systembruker: String,
    val eventTidspunkt: LocalDateTime,
    val fodselsnummer: String,
    val eventId: String,
    val grupperingsId: String,
    val tekst: String,
    val link: String,
    val sikkerhetsnivaa: Int,
    val sistOppdatert: LocalDateTime,
    val aktiv: Boolean,
    val synligFremTil: LocalDateTime,
    val eksternVarsling: Boolean,
    val preferertekanaler: String,
    val namespace: String,
    val appnavn: String,
    val forstbehandlet: LocalDateTime,
    val fristUtlopt: Boolean?
)

data class DatabaseInnboks(
    val id: Long,
    val systembruker: String,
    val eventTidspunkt: LocalDateTime,
    val fodselsnummer: String,
    val eventId: String,
    val grupperingsId: String,
    val tekst: String,
    val link: String,
    val sikkerhetsnivaa: Int,
    val sistOppdatert: LocalDateTime,
    val aktiv: Boolean,
    val eksternVarsling: Boolean,
    val preferertekanaler: String,
    val namespace: String,
    val appnavn: String,
    val forstbehandlet: LocalDateTime,
    val fristUtlopt: Boolean?
)

data class DatabaseEksternVarslingStatus(
    val varselType: VarselType,
    val eventId: String,
    val sistMottattStatus: String,
    val sistOppdatert: LocalDateTime,
    val kanaler: String,
    val eksternVarslingSendt: Boolean,
    val renotifikasjonSendt: Boolean,
    val historikk: List<EksternVarslingHistorikkEntry>,
)

data class DatabaseArkivVarsel(
    val varselType: VarselType,
    val eventId: String,
    val fodselsnummer: String,
    val tekst: String,
    val link: String,
    val sikkerhetsnivaa: Int,
    val aktiv: Boolean,
    val produsentapp: String,
    val eksternvarslingsendt: Boolean,
    val eksternvarslingkanaler: String,
    val forstbehandlet: LocalDateTime,
    val arkivert: LocalDateTime,
    val fristUtlopt: Boolean?
)
