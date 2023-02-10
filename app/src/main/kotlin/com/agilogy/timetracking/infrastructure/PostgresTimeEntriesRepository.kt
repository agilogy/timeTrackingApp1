package com.agilogy.timetracking.infrastructure

import com.agilogy.timetracking.domain.DeveloperProject
import com.agilogy.timetracking.domain.Hours
import com.agilogy.timetracking.domain.TimeEntriesRepository
import com.agilogy.timetracking.domain.TimeEntry
import com.agilogy.db.sql.Sql.batchUpdate
import com.agilogy.db.sql.Sql.select
import com.agilogy.db.sql.Sql.sql
import com.agilogy.db.sql.param
import java.time.Instant
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
            timeEntries.forEach { addBatch(it.developer.param, it.project.param, it.start.param, it.end.param) }
        }
        Unit
    }

    override suspend fun getDeveloperHours(start: Instant, end: Instant): Map<DeveloperProject, Hours> = dataSource.sql {
        val sql = """select developer, project, extract(EPOCH from sum("end" - start)) 
            |from time_entries 
            |where "end" > ? and start < ?
            |group by developer, project""".trimMargin()
        select(sql, start.param, end.param) {
            DeveloperProject(it.string(1)!!, it.string(2)!!) to Hours((it.long(3)!! / 3_600).toInt())
        }
    }.toMap()
}