create table beskjed
(
    id                bigint
        constraint beskjed_pkey
            primary key,
    systembruker      varchar(100),
    eventtidspunkt    timestamp,
    fodselsnummer     varchar(50),
    eventid           varchar(50)
        constraint beskjed_unique_eventid
            unique,
    grupperingsid     varchar(100),
    tekst             varchar(500),
    link              varchar(200),
    sikkerhetsnivaa   integer,
    sistoppdatert     timestamp,
    aktiv             boolean,
    synligfremtil     timestamp,
    uid               varchar(100),
    eksternvarsling   boolean,
    preferertekanaler varchar(100),
    namespace         varchar(100),
    appnavn           varchar(100),
    forstbehandlet    timestamp,
    frist_utløpt      boolean
);

create table oppgave
(
    id                bigint
        constraint oppgave_pkey
            primary key,
    systembruker      varchar(100),
    eventtidspunkt    timestamp,
    fodselsnummer     varchar(50),
    eventid           varchar(50)
        constraint oppgave_unique_eventid
            unique,
    grupperingsid     varchar(100),
    tekst             varchar(500),
    link              varchar(200),
    sikkerhetsnivaa   integer,
    sistoppdatert     timestamp,
    aktiv             boolean,
    eksternvarsling   boolean,
    preferertekanaler varchar(100),
    namespace         varchar(100),
    appnavn           varchar(100),
    synligfremtil     timestamp,
    forstbehandlet    timestamp,
    frist_utløpt      boolean
);

create table innboks
(
    id                bigint
        constraint innboks_pkey
            primary key,
    systembruker      varchar(100),
    eventtidspunkt    timestamp,
    fodselsnummer     varchar(50),
    eventid           varchar(50)
        constraint innboks_unique_eventid
            unique,
    grupperingsid     varchar(100),
    tekst             varchar(500),
    link              varchar(200),
    sikkerhetsnivaa   integer,
    sistoppdatert     timestamp,
    aktiv             boolean,
    namespace         varchar(100),
    appnavn           varchar(100),
    forstbehandlet    timestamp,
    eksternvarsling   boolean,
    preferertekanaler varchar(100),
    frist_utløpt      boolean
);

create table ekstern_varsling_status_beskjed
(
    eventid              varchar(50)
        constraint ekstern_varsling_status_beskjed_eventid_key
            unique
        constraint fk_ekstern_varsling_beskjed_eventid
            references beskjed (eventid),
    sistmottattstatus    text,
    sistoppdatert        timestamp,
    kanaler              text,
    eksternvarslingsendt boolean,
    renotifikasjonsendt  boolean,
    historikk            jsonb
);

create table ekstern_varsling_status_oppgave
(
    eventid              varchar(50)
        constraint ekstern_varsling_status_oppgave_eventid_key
            unique
        constraint fk_ekstern_varsling_oppgave_eventid
            references oppgave (eventid),
    sistmottattstatus    text,
    sistoppdatert        timestamp,
    kanaler              text,
    eksternvarslingsendt boolean,
    renotifikasjonsendt  boolean,
    historikk            jsonb
);

create table ekstern_varsling_status_innboks
(
    eventid              varchar(50)
        constraint ekstern_varsling_status_innboks_eventid_key
            unique
        constraint fk_ekstern_varsling_innboks_eventid
            references innboks (eventid),
    sistmottattstatus    text,
    sistoppdatert        timestamp,
    kanaler              text,
    eksternvarslingsendt boolean,
    renotifikasjonsendt  boolean,
    historikk            jsonb
);

create table beskjed_arkiv
(
    eventid                text,
    fodselsnummer          text,
    tekst                  text,
    link                   text,
    sikkerhetsnivaa        smallint,
    aktiv                  boolean,
    produsentapp           text,
    eksternvarslingsendt   boolean,
    eksternvarslingkanaler text,
    forstbehandlet         timestamp,
    arkivert               timestamp,
    frist_utløpt           boolean
);

create table oppgave_arkiv
(
    eventid                text,
    fodselsnummer          text,
    tekst                  text,
    link                   text,
    sikkerhetsnivaa        smallint,
    aktiv                  boolean,
    produsentapp           text,
    eksternvarslingsendt   boolean,
    eksternvarslingkanaler text,
    forstbehandlet         timestamp,
    arkivert               timestamp,
    frist_utløpt           boolean
);

create table innboks_arkiv
(
    eventid                text,
    fodselsnummer          text,
    tekst                  text,
    link                   text,
    sikkerhetsnivaa        smallint,
    aktiv                  boolean,
    produsentapp           text,
    eksternvarslingsendt   boolean,
    eksternvarslingkanaler text,
    forstbehandlet         timestamp,
    arkivert               timestamp,
    frist_utløpt           boolean
);
