package no.nav.tms.varsel.siphon.database

import kotliquery.queryOf
import no.nav.tms.varsel.siphon.*

fun LocalPostgresDatabase.insertBeskjed(beskjed: DatabaseBeskjed) {
    update {
        queryOf("""
            insert into beskjed(
                id, systembruker, eventtidspunkt, fodselsnummer, eventid, grupperingsid, tekst, link, sikkerhetsnivaa, sistoppdatert, aktiv, synligfremtil, uid, eksternvarsling, preferertekanaler, namespace, appnavn, forstbehandlet, frist_utløpt
            ) values (
                :id, :systembruker, :eventtidspunkt, :fodselsnummer, :eventid, :grupperingsid, :tekst, :link, :sikkerhetsnivaa, :sistoppdatert, :aktiv, :synligfremtil, :uid, :eksternvarsling, :preferertekanaler, :namespace, :appnavn, :forstbehandlet, :fristutlopt
            )
        """,
            mapOf(
                "id" to beskjed.id,
                "systembruker" to beskjed.systembruker,
                "eventtidspunkt" to beskjed.eventTidspunkt,
                "fodselsnummer" to beskjed.fodselsnummer,
                "eventid" to beskjed.eventId,
                "grupperingsid" to beskjed.grupperingsId,
                "tekst" to beskjed.tekst,
                "link" to beskjed.link,
                "sikkerhetsnivaa" to beskjed.sikkerhetsnivaa,
                "sistoppdatert" to beskjed.sistOppdatert,
                "aktiv" to beskjed.aktiv,
                "synligfremtil" to beskjed.synligFremTil,
                "uid" to beskjed.uid,
                "eksternvarsling" to beskjed.eksternVarsling,
                "preferertekanaler" to beskjed.preferertekanaler,
                "namespace" to beskjed.namespace,
                "appnavn" to beskjed.appnavn,
                "forstbehandlet" to beskjed.forstbehandlet,
                "fristutlopt" to beskjed.fristUtlopt,
            )
        )
    }
}

fun LocalPostgresDatabase.insertOppgave(oppgave: DatabaseOppgave) {
    update {
        queryOf("""
            insert into oppgave(
                id, systembruker, eventtidspunkt, fodselsnummer, eventid, grupperingsid, tekst, link, sikkerhetsnivaa, sistoppdatert, aktiv, synligfremtil, eksternvarsling, preferertekanaler, namespace, appnavn, forstbehandlet, frist_utløpt
            ) values (
                :id, :systembruker, :eventtidspunkt, :fodselsnummer, :eventid, :grupperingsid, :tekst, :link, :sikkerhetsnivaa, :sistoppdatert, :aktiv, :synligfremtil, :eksternvarsling, :preferertekanaler, :namespace, :appnavn, :forstbehandlet, :fristutlopt
            )
        """,
            mapOf(
                "id" to oppgave.id,
                "systembruker" to oppgave.systembruker,
                "eventtidspunkt" to oppgave.eventTidspunkt,
                "fodselsnummer" to oppgave.fodselsnummer,
                "eventid" to oppgave.eventId,
                "grupperingsid" to oppgave.grupperingsId,
                "tekst" to oppgave.tekst,
                "link" to oppgave.link,
                "sikkerhetsnivaa" to oppgave.sikkerhetsnivaa,
                "sistoppdatert" to oppgave.sistOppdatert,
                "aktiv" to oppgave.aktiv,
                "synligfremtil" to oppgave.synligFremTil,
                "eksternvarsling" to oppgave.eksternVarsling,
                "preferertekanaler" to oppgave.preferertekanaler,
                "namespace" to oppgave.namespace,
                "appnavn" to oppgave.appnavn,
                "forstbehandlet" to oppgave.forstbehandlet,
                "fristutlopt" to oppgave.fristUtlopt,
            )
        )
    }
}

fun LocalPostgresDatabase.insertInnboks(innboks: DatabaseInnboks) {
    update {
        queryOf("""
            insert into innboks(
                id, systembruker, eventtidspunkt, fodselsnummer, eventid, grupperingsid, tekst, link, sikkerhetsnivaa, sistoppdatert, aktiv, eksternvarsling, preferertekanaler, namespace, appnavn, forstbehandlet, frist_utløpt
            ) values (
                :id, :systembruker, :eventtidspunkt, :fodselsnummer, :eventid, :grupperingsid, :tekst, :link, :sikkerhetsnivaa, :sistoppdatert, :aktiv, :eksternvarsling, :preferertekanaler, :namespace, :appnavn, :forstbehandlet, :fristutlopt
            )
        """,
            mapOf(
                "id" to innboks.id,
                "systembruker" to innboks.systembruker,
                "eventtidspunkt" to innboks.eventTidspunkt,
                "fodselsnummer" to innboks.fodselsnummer,
                "eventid" to innboks.eventId,
                "grupperingsid" to innboks.grupperingsId,
                "tekst" to innboks.tekst,
                "link" to innboks.link,
                "sikkerhetsnivaa" to innboks.sikkerhetsnivaa,
                "sistoppdatert" to innboks.sistOppdatert,
                "aktiv" to innboks.aktiv,
                "eksternvarsling" to innboks.eksternVarsling,
                "preferertekanaler" to innboks.preferertekanaler,
                "namespace" to innboks.namespace,
                "appnavn" to innboks.appnavn,
                "forstbehandlet" to innboks.forstbehandlet,
                "fristutlopt" to innboks.fristUtlopt,
            )
        )
    }
}

fun LocalPostgresDatabase.insertEksternVarslingStatus(doknotStatus: DatabaseEksternVarslingStatus) {
    update {
        queryOf("""
            insert into ekstern_varsling_status_${doknotStatus.varselType.lowercaseName} (
                eventid, sistmottattstatus, sistoppdatert, kanaler, eksternvarslingsendt, renotifikasjonsendt, historikk
            ) values (
                :eventid, :sistmottattstatus, :sistoppdatert, :kanaler, :eksternvarslingsendt, :renotifikasjonsendt, :historikk
            )
        """,
            mapOf(
                "eventid" to doknotStatus.eventId,
                "sistmottattstatus" to doknotStatus.sistMottattStatus,
                "sistoppdatert" to doknotStatus.sistOppdatert,
                "kanaler" to doknotStatus.kanaler,
                "eksternvarslingsendt" to doknotStatus.eksternVarslingSendt,
                "renotifikasjonsendt" to doknotStatus.renotifikasjonSendt,
                "historikk" to doknotStatus.historikk.toJsonb()
            )
        )
    }
}

fun LocalPostgresDatabase.insertArkivVarsel(arkivVarsel: DatabaseArkivVarsel) {
    update {
        queryOf("""
           insert into ${arkivVarsel.varselType.lowercaseName}_arkiv (
               eventid, fodselsnummer, tekst, link, sikkerhetsnivaa, aktiv, produsentapp, eksternvarslingsendt, eksternvarslingkanaler, forstbehandlet, arkivert, frist_utløpt
           ) values (
               :eventid, :fodselsnummer, :tekst, :link, :sikkerhetsnivaa, :aktiv, :produsentapp, :eksternvarslingsendt, :eksternvarslingkanaler, :forstbehandlet, :arkivert, :fristutlopt
           )
        """,
            mapOf(
                "eventid" to arkivVarsel.eventId,
                "fodselsnummer" to arkivVarsel.fodselsnummer,
                "tekst" to arkivVarsel.tekst,
                "link" to arkivVarsel.link,
                "sikkerhetsnivaa" to arkivVarsel.sikkerhetsnivaa,
                "aktiv" to arkivVarsel.aktiv,
                "produsentapp" to arkivVarsel.produsentapp,
                "eksternvarslingsendt" to arkivVarsel.eksternvarslingsendt,
                "eksternvarslingkanaler" to arkivVarsel.eksternvarslingkanaler,
                "forstbehandlet" to arkivVarsel.forstbehandlet,
                "arkivert" to arkivVarsel.arkivert,
                "frist_utløpt" to arkivVarsel.fristUtlopt
            )
        )
    }
}
