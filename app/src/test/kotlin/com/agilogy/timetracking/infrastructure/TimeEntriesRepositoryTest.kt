package com.agilogy.timetracking.infrastructure

import arrow.fx.coroutines.use
import com.agilogy.db.hikari.HikariCp
import com.agilogy.db.postgresql.PostgreSql
import com.agilogy.db.sql.Sql.sql
import com.agilogy.db.sql.Sql.update
import com.agilogy.timetracking.domain.DeveloperProject
import com.agilogy.timetracking.domain.Hours
import com.agilogy.timetracking.domain.TimeEntriesRepository
import com.agilogy.timetracking.domain.TimeEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestScope
import org.junit.jupiter.api.Assertions.assertEquals
import org.postgresql.util.PSQLException
import java.time.Instant
import javax.sql.DataSource

class TimeEntriesRepositoryTest : FunSpec() {

    private suspend fun <A> withTestDataSource(database: String? = "test", f: suspend (DataSource) -> A) =
        HikariCp.dataSource("postgres", "postgres", "localhost", database = database).use { dataSource -> f(dataSource) }

    private suspend fun <A> withPostgresTestRepo(f: suspend (TimeEntriesRepository) -> A) =
        withTestDataSource { f(PostgresTimeEntriesRepository(it)) }

    private suspend fun <A> withInMemoryTestRepo(f: suspend (TimeEntriesRepository) -> A) =
        f(InMemoryTimeEntriesRepository())

    private fun <A> Result<A>.recoverIf(value: A, predicate: (Throwable) -> Boolean): Result<A> =
        this.recoverCatching { if (predicate(it)) value else throw it }

    init {

        beforeTest {
            withTestDataSource(null) { dataSource ->
                kotlin.runCatching { dataSource.sql { update("create database test") } }
                    .recoverIf(Unit) { it is PSQLException && it.sqlState == PostgreSql.DuplicateDatabase }.getOrThrow()
            }

            withTestDataSource { dataSource ->
                kotlin.runCatching { dataSource.sql { update("drop table time_entries") } }
                    .recoverIf(Unit) { it is PSQLException && it.sqlState == PostgreSql.UndefinedTable }.getOrThrow()
                PostgresTimeEntriesRepository.dbMigrations.forEach { dbMigration -> dataSource.sql { update(dbMigration) } }
            }

        }

        fun test(name: String, test: suspend TestScope.(TimeEntriesRepository) -> Unit) {
            context(name) {
                this.test("Postgres") {
                    withPostgresTestRepo { test(it) }
                }
                this.test("In Memory") {
                    withInMemoryTestRepo { test(it) }
                }
            }
        }

        test("Get developer hours") { repo ->
            val now = Instant.now()
            val oneHourAgo = now.minusSeconds(3600)
            repo.saveTimeEntries(
                listOf(
                    TimeEntry("d1", "p", oneHourAgo, now),
                    TimeEntry("d2", "p", oneHourAgo, now),
                )
            )
            assertEquals(
                mapOf(
                    DeveloperProject("d1", "p") to Hours(1),
                    DeveloperProject("d2", "p") to Hours(1),
                ),
                repo.getDeveloperHours(oneHourAgo, now)
            )

        }
    }
}