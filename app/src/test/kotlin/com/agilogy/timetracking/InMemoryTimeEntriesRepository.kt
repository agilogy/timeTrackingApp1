package com.agilogy.timetracking

import com.agilogy.timetracking.domain.DeveloperProject
import com.agilogy.timetracking.domain.Hours
import com.agilogy.timetracking.domain.TimeEntriesRepository
import com.agilogy.timetracking.domain.TimeEntry
import java.lang.Long.max
import java.lang.Long.min
import java.time.Instant
import kotlin.math.roundToInt

class InMemoryTimeEntriesRepository(initialState: List<TimeEntry> = emptyList()): TimeEntriesRepository {


    private val state = mutableListOf<TimeEntry>()
    init{
        state.addAll(initialState)
    }

    override fun saveTimeEntries(timeEntries: List<TimeEntry>) {
        state.addAll(timeEntries)
    }

    override fun getDeveloperHours(start: Instant, end: Instant): Map<DeveloperProject, Hours>  =
        state.map { timeEntry ->
            val s = max(start.toEpochMilli(), timeEntry.start.toEpochMilli())
            val e = min(end.toEpochMilli(), timeEntry.end.toEpochMilli())
            DeveloperProject(timeEntry.developer, timeEntry.project) to max (e - s, 0)
        }.groupBy({ it.first }) {it.second}.mapValues { Hours( (it.value.sum()/3_600_000.0).roundToInt()) }

    fun getState(): List<TimeEntry> = state.toList()

}
