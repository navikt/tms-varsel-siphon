package no.nav.tms.varsel.siphon.database

import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotliquery.queryOf
import org.postgresql.util.PSQLException
import org.testcontainers.containers.PostgreSQLContainer

class LocalPostgresDatabase private constructor() : Database {

    private val memDataSource: HikariDataSource
    private val container = PostgreSQLContainer<Nothing>("postgres:14.5")

    companion object {
        private val instance by lazy {
            LocalPostgresDatabase().also {
                it.createTables()
            }
        }

        fun cleanDb(): LocalPostgresDatabase {
            deleteFromTables(instance)
            return instance
        }

        fun deleteFromTables(database: LocalPostgresDatabase) {
            database.update { queryOf("delete from ekstern_varsling_status_beskjed") }
            database.update { queryOf("delete from beskjed") }
            database.update { queryOf("delete from beskjed_arkiv") }
            database.update { queryOf("delete from ekstern_varsling_status_oppgave") }
            database.update { queryOf("delete from oppgave") }
            database.update { queryOf("delete from oppgave_arkiv") }
            database.update { queryOf("delete from ekstern_varsling_status_innboks") }
            database.update { queryOf("delete from innboks") }
            database.update { queryOf("delete from innboks_arkiv") }
        }
    }

    init {
        container.start()
        memDataSource = createDataSource()
    }

    override val dataSource: HikariDataSource
        get() = memDataSource

    private fun createDataSource(): HikariDataSource {
        return HikariDataSource().apply {
            jdbcUrl = container.jdbcUrl
            username = container.username
            password = container.password
            isAutoCommit = true
            validate()
        }
    }

    private fun createTables() = runBlocking {
        withTimeout(3000) {
            while (true) {
                try {
                    val fileContent = this::class.java.getResource("/db/V1.0.0__brukernotifikasjon_tables.sql")!!.readText()
                    update { queryOf(fileContent) }
                    return@withTimeout
                } catch (_: PSQLException) {
                    delay(100)
                }
            }
        }
    }
}

