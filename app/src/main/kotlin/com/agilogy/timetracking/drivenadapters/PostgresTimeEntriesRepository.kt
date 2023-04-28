package com.agilogy.timetracking.drivenadapters

import com.agilogy.db.sql.ResultSetView
import com.agilogy.db.sql.Sql.batchUpdate
import com.agilogy.db.sql.Sql.select
import com.agilogy.db.sql.Sql.sql
import com.agilogy.db.sql.SqlParameter
import com.agilogy.db.sql.param
import com.agilogy.time.toInstantRange
import com.agilogy.timetracking.domain.DeveloperName
import com.agilogy.timetracking.domain.Hours
import com.agilogy.timetracking.domain.ProjectName
import com.agilogy.timetracking.domain.TimeEntriesRepository
import com.agilogy.timetracking.domain.TimeEntry
import java.time.Instant
import java.time.LocalDate
import javax.sql.DataSource
import kotlin.math.ceil

class PostgresTimeEntriesRepository(private val dataSource: DataSource) : TimeEntriesRepository {

    private val DeveloperName.param: SqlParameter get() = name.param
    private val ProjectName.param: SqlParameter get() = name.param
    private fun ResultSetView.developer(columnIndex: Int): DeveloperName? = string(columnIndex)?.let { DeveloperName(it) }
    private fun ResultSetView.project(columnIndex: Int): ProjectName? = string(columnIndex)?.let { ProjectName(it) }

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
            timeEntries.forEach {
                addBatch(
                    it.developer.param, it.project.param, it.range.start.param, it.range.endInclusive
                        .param
                )
            }
        }
        Unit
    }

    override suspend fun getHoursByDeveloperAndProject(range: ClosedRange<Instant>): Map<Pair<DeveloperName, ProjectName>, Hours> = dataSource.sql {
        val sql = """select developer, project, extract(EPOCH from sum(least("end", ?) - greatest(start, ?))) 
            |from time_entries 
            |where "end" > ? and start < ?
            |group by developer, project""".trimMargin()
        select(sql, range.endInclusive.param, range.start.param, range.start.param, range.endInclusive.param) {
            (it.developer(1)!! to it.project(2)!!) to Hours(ceil(it.long(3)!! / 3_600.0).toInt())
        }.toMap().filterValues { it.value > 0 }
    }

    override suspend fun getDeveloperHoursByProjectAndDate(
        developer: DeveloperName,
        dateRange: ClosedRange<LocalDate>,
    ): List<Triple<LocalDate, ProjectName, Hours>> = dataSource.sql {
        val instantRange = dateRange.toInstantRange()
        val sql = """select date(start at time zone 'CEST'), project, extract(EPOCH from sum("end" - start)) 
            |from time_entries 
            |where "start" > ? and start < ? and developer = ?
            |group by date(start at time zone 'CEST'), project
            |order by date(start at time zone 'CEST'), project
            |""".trimMargin()
        select(sql, instantRange.start.param, instantRange.endInclusive.param, developer.param) {
            Triple(LocalDate.parse(it.string(1)!!), it.project(2)!!, Hours((it.long(3)!! / 3_600).toInt()))
        }
    }

    override suspend fun listTimeEntries(timeRange: ClosedRange<Instant>, developer: DeveloperName?): List<TimeEntry> = dataSource.sql {
        val sql = """select developer, project, start, "end" 
            |from time_entries 
            |where "end" > ? and start < ?""".trimMargin()
        select(sql, timeRange.start.param, timeRange.endInclusive.param) {
            TimeEntry(it.developer(1)!!, it.project(2)!!, it.timestamp(3)!!..it.timestamp(4)!!)
        }
    }
}