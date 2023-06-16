package no.nav.tms.varsel.siphon

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import no.nav.tms.varsel.siphon.database.Database


fun Route.metaRoutes(database: Database) {

    get("/internal/isAlive") {
        call.respondText(text = "ALIVE", contentType = ContentType.Text.Plain)
    }

    get("/internal/isReady") {
        if (database.isConnected()) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.ServiceUnavailable)
        }
    }
}
