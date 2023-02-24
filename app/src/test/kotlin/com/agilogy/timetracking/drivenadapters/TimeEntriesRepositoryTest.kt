package com.agilogy.timetracking.drivenadapters

import arrow.fx.coroutines.use
import com.agilogy.db.hikari.HikariCp
import com.agilogy.db.postgresql.PostgreSql
import com.agilogy.db.sql.Sql.sql
import com.agilogy.db.sql.Sql.update
import com.agilogy.timetracking.domain.Developer
import com.agilogy.timetracking.domain.Hours
import com.agilogy.timetracking.domain.Project
import com.agilogy.timetracking.domain.TimeEntriesRepository
import com.agilogy.timetracking.domain.TimeEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestScope
import org.junit.jupiter.api.Assertions.assertEquals
import org.postgresql.util.PSQLException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import javax.sql.DataSource

class TimeEntriesRepositoryTest : FunSpec() {

    private suspend fun <A> withTestDataSource(database: String? = "test", f: suspend (DataSource) -> A) =
        HikariCp.dataSource("jdbc:postgresql://localhost/${database ?: ""}", "postgres", "postgres").use {
                dataSource -> f(dataSource)
        }

    private suspend fun <A> withPostgresTestRepo(f: suspend (TimeEntriesRepository) -> A) =
        withTestDataSource { f(PostgresTimeEntriesRepository(it)) }

    private suspend fun <A> withInMemoryTestRepo(f: suspend (TimeEntriesRepository) -> A) =
        f(InMemoryTimeEntriesRepository())

    private fun <A> Result<A>.recoverIf(value: A, predicate: (Throwable) -> Boolean): Result<A> =
        this.recoverCatching { if (predicate(it)) value else throw it }

    private fun LocalDateTime.toLocalInstant() = atZone(ZoneOffset.systemDefault()).toInstant()
    private fun LocalDate.toLocalInstant() = atTime(0, 0).toLocalInstant()
    private fun date(day: Int): LocalDate = LocalDate.of(2013, 2, day)
    private fun timePeriod(day: Int, hourFrom: Int, hours: Int): ClosedRange<Instant> {
        val from = date(day).atTime(LocalTime.of(hourFrom, 0)).toLocalInstant()
        return (from..from.plusSeconds(3600L * hours))
    }

    init {

        val d1 = Developer("d1")
        val d2 = Developer("d2")
        val p = Project("p")
        val p2 = Project("p2")

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

        test("getHoursByDeveloperAndProject") { repo ->
            val testDay = date(1)
            repo.saveTimeEntries(
                listOf(
                    TimeEntry(d1, p, timePeriod(1, 9, 4)),
                    TimeEntry(d1, p, timePeriod(1, 14, 3)),
                    TimeEntry(d2, p, timePeriod(1, 10, 3)),
                )
            )
            assertEquals(
                mapOf(
                    Pair(d1, p) to Hours(7),
                    Pair(d2, p) to Hours(3),
                ),
                repo.getHoursByDeveloperAndProject(testDay.toLocalInstant()..testDay.plusDays(1).toLocalInstant())
            )

        }

        test("getDeveloperHoursByProjectAndDate") { repo ->
            repo.saveTimeEntries(
                listOf(
                    TimeEntry(d1, p, timePeriod(1, 9, 1)),
                    TimeEntry(d1, p, timePeriod(1, 11, 2)),
                    TimeEntry(d1, p2, timePeriod(1, 14, 4)),
                    TimeEntry(d1, p, timePeriod(2, 8, 6)),
                )
            )

            assertEquals(
                listOf(
                    Triple(date(1), p, Hours(3)),
                    Triple(date(1), p2, Hours(4)),
                    Triple(date(2), p, Hours(6))
                ),
                repo.getDeveloperHoursByProjectAndDate(d1, date(1)..date(2))
            )

        }
    }
}