@file:UseSerializers(ZonedDateTimeSerializer::class)
package no.nav.tms.varsel.siphon

import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.tms.varsel.siphon.VarselType
import java.lang.Integer.min
import java.time.ZonedDateTime

fun Route.debugApi(readRepository: VarselRepository) {
    get<DebugVarsler> { params ->
        call.respond(readRepository.fetchVarselList(
            type = params.type,
            max = min(params.max, 10),
            fromDate = ZonedDateTime.now().minusYears(1),
            toDate = ZonedDateTime.now()
        ))
    }
}

fun Route.varselApi(readRepository: VarselRepository) {
    get<Varsler> { params ->
        call.respond(readRepository.fetchVarselList(
            type = params.type,
            fromDate = params.fraDato,
            toDate = params.tilDato,
            max = params.max
        ))
    }

    get<ArkivVarsler> { params ->
        call.respond(readRepository.fetchArchivedVarelList(
            type = params.type,
            fromDate = params.fraDato,
            toDate = params.tilDato,
            max = params.max
        ))
    }
}

@Serializable
@Resource("/debug/varsler")
class DebugVarsler(
    val type: VarselType,
    val max: Int
)

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
