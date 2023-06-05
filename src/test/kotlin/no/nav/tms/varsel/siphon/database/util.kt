package no.nav.tms.varsel.siphon.database

import com.fasterxml.jackson.databind.ObjectMapper
import org.postgresql.util.PGobject

fun Any?.toJsonb(objectMapper: ObjectMapper = defaultObjectMapper()): PGobject? {
    return if (this == null) {
        null
    } else {
        objectMapper.writeValueAsString(this).let {
            PGobject().apply {
                type = "jsonb"
                value = it
            }
        }
    }
}
