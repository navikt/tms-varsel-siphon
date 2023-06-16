package no.nav.tms.varsel.siphon

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import no.nav.tms.token.support.azure.validation.installAzureAuth
import no.nav.tms.varsel.siphon.database.PostgresDatabase

fun main() {
    val environment = Environment()
    val database = PostgresDatabase(environment)

    val varselRepository = VarselRepository(database)

    embeddedServer(
        factory = Netty,
        environment = applicationEngineEnvironment {
            module {
                configureApi(varselRepository)
            }
            connector {
                port = 8080
            }
        }
    ).start(wait = true)
}

fun Application.configureApi(
    varselRepository: VarselRepository,
    installAuthenticatorsFunction: Application.() -> Unit = installAuth(),
) {

    val log = KotlinLogging.logger {}

    installAuthenticatorsFunction()

    install(Resources)

    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                is IllegalArgumentException -> {
                    call.respondText(
                        status = HttpStatusCode.BadRequest,
                        text = cause.message ?: "Feil i parametre"
                    )

                    log.warn("Feil i parametere.", cause)
                }

                else -> {
                    call.respond(HttpStatusCode.InternalServerError)
                    log.warn("Feil ved behandling av forespørsel.", cause)
                }
            }

        }
    }

    routing {
        metaRoutes()
        authenticate {
            varselApi(varselRepository)
        }
    }
}

private fun installAuth(): Application.() -> Unit = {
    installAzureAuth {
        setAsDefault = true
    }
}
