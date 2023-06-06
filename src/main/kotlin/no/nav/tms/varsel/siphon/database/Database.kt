package no.nav.tms.varsel.siphon.database

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.zaxxer.hikari.HikariDataSource
import kotliquery.*
import kotliquery.action.ListResultQueryAction
import kotliquery.action.NullableResultQueryAction

interface Database {

    val dataSource: HikariDataSource

    fun update(queryBuilder: (Session) -> Query) {
        using(sessionOf(dataSource)) {
            it.run(queryBuilder.invoke(it).asUpdate)
        }
    }

    fun <T> singleOrNull(action: () -> NullableResultQueryAction<T>): T? =
        using(sessionOf(dataSource)) {
            it.run(action.invoke())
        }

    fun <T> list(action: () -> ListResultQueryAction<T>): List<T> =
        using(sessionOf(dataSource)) {
            it.run(action.invoke())
        }
}

inline fun <reified T> Row.json(label: String, objectMapper: ObjectMapper = defaultObjectMapper()): T {
    return objectMapper.readValue(string(label))
}

inline fun <reified T> Row.jsonOrNull(label: String, objectMapper: ObjectMapper = defaultObjectMapper()): T? {
    return stringOrNull(label)?.let { objectMapper.readValue(it) }
}
