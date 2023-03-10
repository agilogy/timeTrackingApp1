package com.agilogy.timetracking.drivenadapters

import com.agilogy.timetracking.domain.DeveloperProject
import com.agilogy.timetracking.domain.Hours
import com.agilogy.timetracking.domain.TimeEntriesRepository
import com.agilogy.timetracking.domain.TimeEntry
import com.agilogy.db.sql.Sql.batchUpdate
import com.agilogy.db.sql.Sql.select
import com.agilogy.db.sql.Sql.sql
import com.agilogy.db.sql.param
import com.agilogy.timetracking.domain.toInstantRange
import java.time.Instant
import java.time.LocalDate
import javax.sql.DataSource

class PostgresTimeEntriesRepository(private val dataSource: DataSource) : TimeEntriesRepository {

    companion object {
        val dbMigrations = listOf(
            """create table time_entries(
            |id serial,
            |developer text not null, 
            |project text not null, 
            |start timestamptz not null, 
            |"end" timestamptz not null
           )""".trimMargin()
        )
    }

    override suspend fun saveTimeEntries(timeEntries: List<TimeEntry>) = dataSource.sql {
        val sql = """insert into time_entries(developer, project, start, "end") values (?, ?, ?, ?)"""
        batchUpdate(sql) {
            timeEntries.forEach { addBatch(it.developer.param, it.project.param, it.range.start.param, it.range.endInclusive.param) }
        }
        Unit
    }

    override suspend fun getHoursByDeveloperAndProject(range: ClosedRange<Instant>): Map<DeveloperProject, Hours> = dataSource.sql {
        val sql = """select developer, project, extract(EPOCH from sum("end" - start)) 
            |from time_entries 
            |where "end" > ? and start < ?
            |group by developer, project""".trimMargin()
        select(sql, range.start.param, range.endInclusive.param) {
            DeveloperProject(it.string(1)!!, it.string(2)!!) to Hours((it.long(3)!! / 3_600).toInt())
        }
        }.toMap()

    override suspend fun getDeveloperHoursByProjectAndDate(
        developer: String,
        dateRange: ClosedRange<LocalDate>,
    ): List<Triple<LocalDate, String, Hours>> = dataSource.sql {
        val instantRange = dateRange.toInstantRange()
        val sql = """select date(start at time zone 'CEST'), project, extract(EPOCH from sum("end" - start)) 
            |from time_entries 
            |where "start" > ? and start < ?
            |group by date(start at time zone 'CEST'), project
            |order by date(start at time zone 'CEST'), project
            |""".trimMargin()
        select(sql, instantRange.start.param, instantRange.endInclusive.param) {
            Triple(LocalDate.parse(it.string(1)!!), it.string(2)!!, Hours((it.long(3)!! / 3_600).toInt()))
        }
    }
}