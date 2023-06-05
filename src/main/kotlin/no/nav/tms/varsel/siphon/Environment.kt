package no.nav.tms.varsel.siphon

import no.nav.personbruker.dittnav.common.util.config.StringEnvVar.getEnvVar

data class Environment(
    val dbUser: String = getEnvVar("DB_USERNAME"),
    val dbPassword: String = getEnvVar("DB_PASSWORD"),
    val dbUrl: String = getDbUrl(),
)

fun getDbUrl(): String {
    val host: String = getEnvVar("DB_HOST")
    val port: String = getEnvVar("DB_PORT")
    val name: String = getEnvVar("DB_DATABASE")

    return if (host.endsWith(":$port")) {
        "jdbc:postgresql://${host}/$name"
    } else {
        "jdbc:postgresql://${host}:${port}/${name}"
    }
}
