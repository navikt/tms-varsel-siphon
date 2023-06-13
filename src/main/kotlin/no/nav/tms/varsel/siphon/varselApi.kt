@file:UseSerializers(ZonedDateTimeSerializer::class)
package no.nav.tms.varsel.siphon

import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import mu.KotlinLogging
import java.time.ZonedDateTime

private val log = KotlinLogging.logger{}

fun Route.varselApi(readRepository: VarselRepository) {
    get<Varsler> { params ->
        log.info("Fetching varsler for params $params")
        call.respond(readRepository.fetchVarselList(
            type = params.type,
            fromDate = params.fraDato,
            toDate = params.tilDato,
            max = params.max
        ))
    }

    get<ArkivVarsler> { params ->
        log.info("Fetching arkiv-varsler for params $params")
        call.respond(readRepository.fetchArvivertVarselList(
            type = params.type,
            fromDate = params.fraDato,
            toDate = params.tilDato,
            max = params.max
        ))
    }
}

@Serializable
@Resource("/varsler")
class Varsler(
    val type: VarselType,
    val fraDato: ZonedDateTime,
    val tilDato: ZonedDateTime,
    val max: Int
)

@Serializable
@Resource("/arkiv/varsler")
class ArkivVarsler(
    val type: VarselType,
    val fraDato: ZonedDateTime,
    val tilDato: ZonedDateTime,
    val max: Int
)
