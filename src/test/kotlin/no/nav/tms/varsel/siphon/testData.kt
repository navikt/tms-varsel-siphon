package no.nav.tms.varsel.siphon

import no.nav.tms.varsel.siphon.LocalDateTimeHelper.nowAtUtc
import no.nav.tms.varsel.siphon.ZonedDateTimeHelper.nowAtUtcZ
import java.time.LocalDateTime
import java.util.*

private var varselId = 1L

fun dbBeskjed(
    eventId: String = UUID.randomUUID().toString(),
    systembruker: String = "systembruker",
    eventTidspunkt: LocalDateTime = nowAtUtc(),
    fodselsnummer: String = "fodselsnummer",
    grupperingsId: String = "grupperingsId",
    tekst: String = "tekst",
    link: String = "link",
    sikkerhetsnivaa: Int = 4,
    sistOppdatert: LocalDateTime = nowAtUtc(),
    aktiv: Boolean = true,
    synligFremTil: LocalDateTime = nowAtUtc(),
    uid: String = UUID.randomUUID().toString(),
    eksternVarsling: Boolean = true,
    preferertekanaler: String = "SMS,EPOST",
    namespace: String = "namespace",
    appnavn: String = "appnavn",
    forstbehandlet: LocalDateTime = nowAtUtc(),
    fristUtlopt: Boolean = false,
) = DatabaseBeskjed(
    id = varselId++,
    systembruker = systembruker,
    eventTidspunkt = eventTidspunkt,
    fodselsnummer = fodselsnummer,
    eventId = eventId,
    grupperingsId = grupperingsId,
    tekst = tekst,
    link = link,
    sikkerhetsnivaa = sikkerhetsnivaa,
    sistOppdatert = sistOppdatert,
    aktiv = aktiv,
    synligFremTil = synligFremTil,
    uid = uid,
    eksternVarsling = eksternVarsling,
    preferertekanaler = preferertekanaler,
    namespace = namespace,
    appnavn = appnavn,
    forstbehandlet = forstbehandlet,
    fristUtlopt = fristUtlopt
)

fun dbOppgave(
    eventId: String = UUID.randomUUID().toString(),
    systembruker: String = "systembruker",
    eventTidspunkt: LocalDateTime = nowAtUtc(),
    fodselsnummer: String = "fodselsnummer",
    grupperingsId: String = "grupperingsId",
    tekst: String = "tekst",
    link: String = "link",
    sikkerhetsnivaa: Int = 4,
    sistOppdatert: LocalDateTime = nowAtUtc(),
    aktiv: Boolean = true,
    synligFremTil: LocalDateTime = nowAtUtc(),
    eksternVarsling: Boolean = true,
    preferertekanaler: String = "SMS,EPOST",
    namespace: String = "namespace",
    appnavn: String = "appnavn",
    forstbehandlet: LocalDateTime = nowAtUtc(),
    fristUtlopt: Boolean = false,
) = DatabaseOppgave(
    id = varselId++,
    systembruker = systembruker,
    eventTidspunkt = eventTidspunkt,
    fodselsnummer = fodselsnummer,
    eventId = eventId,
    grupperingsId = grupperingsId,
    tekst = tekst,
    link = link,
    sikkerhetsnivaa = sikkerhetsnivaa,
    sistOppdatert = sistOppdatert,
    aktiv = aktiv,
    synligFremTil = synligFremTil,
    eksternVarsling = eksternVarsling,
    preferertekanaler = preferertekanaler,
    namespace = namespace,
    appnavn = appnavn,
    forstbehandlet = forstbehandlet,
    fristUtlopt = fristUtlopt
)

fun dbInnboks(
    eventId: String = UUID.randomUUID().toString(),
    systembruker: String = "systembruker",
    eventTidspunkt: LocalDateTime = nowAtUtc(),
    fodselsnummer: String = "fodselsnummer",
    grupperingsId: String = "grupperingsId",
    tekst: String = "tekst",
    link: String = "link",
    sikkerhetsnivaa: Int = 4,
    sistOppdatert: LocalDateTime = nowAtUtc(),
    aktiv: Boolean = true,
    eksternVarsling: Boolean = true,
    preferertekanaler: String = "SMS,EPOST",
    namespace: String = "namespace",
    appnavn: String = "appnavn",
    forstbehandlet: LocalDateTime = nowAtUtc(),
    fristUtlopt: Boolean = false,
) = DatabaseInnboks(
    id = varselId++,
    systembruker = systembruker,
    eventTidspunkt = eventTidspunkt,
    fodselsnummer = fodselsnummer,
    eventId = eventId,
    grupperingsId = grupperingsId,
    tekst = tekst,
    link = link,
    sikkerhetsnivaa = sikkerhetsnivaa,
    sistOppdatert = sistOppdatert,
    aktiv = aktiv,
    eksternVarsling = eksternVarsling,
    preferertekanaler = preferertekanaler,
    namespace = namespace,
    appnavn = appnavn,
    forstbehandlet = forstbehandlet,
    fristUtlopt = fristUtlopt
)

fun dbEksternVarslingStatus(
    varselType: VarselType,
    eventId: String,
    sistMottattStatus: String = "OVERSENDT",
    sistOppdatert: LocalDateTime = nowAtUtc(),
    kanaler: String = "SMS,EPOST",
    eksternVarslingSendt: Boolean = true,
    renotifikasjonSendt: Boolean = false,
    historikk: List<EksternVarslingHistorikkEntry> = eksternVarslingHistorikk,
) = DatabaseEksternVarslingStatus(
    varselType = varselType,
    eventId = eventId,
    sistMottattStatus = sistMottattStatus,
    sistOppdatert = sistOppdatert,
    kanaler = kanaler,
    eksternVarslingSendt = eksternVarslingSendt,
    renotifikasjonSendt = renotifikasjonSendt,
    historikk = historikk
)

private val eksternVarslingHistorikk = listOf(
    EksternVarslingHistorikkEntry(
        melding = "Oversendt",
        status = EksternStatus.bestilt,
        distribusjonsId = null,
        kanal = null,
        renotifikasjon = null,
        tidspunkt = nowAtUtcZ()
    ),
    EksternVarslingHistorikkEntry(
        melding = "Ferdigstilt",
        status = EksternStatus.sendt,
        distribusjonsId = 123L,
        kanal = "SMS",
        renotifikasjon = false,
        tidspunkt = nowAtUtcZ()
    ),
    EksternVarslingHistorikkEntry(
        melding = "Ferdigstilt",
        status = EksternStatus.sendt,
        distribusjonsId = 123L,
        kanal = "EPOST",
        renotifikasjon = false,
        tidspunkt = nowAtUtcZ()
    )
)

fun dbArkivVarsel(
    varselType: VarselType,
    eventid: String = UUID.randomUUID().toString(),
    fodselsnummer: String = "fodselsnummer",
    tekst: String = "tekst",
    link: String = "link",
    sikkerhetsnivaa: Int = 4,
    aktiv: Boolean = false,
    produsentapp: String = "produsentapp",
    eksternvarslingsendt: Boolean = true,
    eksternvarslingkanaler: String = "SMS,EPOST",
    forstbehandlet: LocalDateTime = nowAtUtc(),
    arkivert: LocalDateTime = nowAtUtc(),
    fristUtlopt: Boolean? = null
) = DatabaseArkivVarsel(
    varselType = varselType,
    eventId = eventid,
    fodselsnummer = fodselsnummer,
    tekst = tekst,
    link = link,
    sikkerhetsnivaa = sikkerhetsnivaa,
    aktiv = aktiv,
    produsentapp = produsentapp,
    eksternvarslingsendt = eksternvarslingsendt,
    eksternvarslingkanaler = eksternvarslingkanaler,
    forstbehandlet = forstbehandlet,
    arkivert = arkivert,
    fristUtlopt = fristUtlopt
)
