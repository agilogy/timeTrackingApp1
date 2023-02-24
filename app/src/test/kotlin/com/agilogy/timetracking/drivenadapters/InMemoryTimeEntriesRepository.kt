package com.agilogy.timetracking.drivenadapters

import com.agilogy.time.intersection
import com.agilogy.time.sum
import com.agilogy.time.toInstantRange
import com.agilogy.timetracking.domain.Developer
import com.agilogy.timetracking.domain.Hours
import com.agilogy.timetracking.domain.Project
import com.agilogy.timetracking.domain.TimeEntriesRepository
import com.agilogy.timetracking.domain.TimeEntry
import java.time.Instant
import java.time.LocalDate
import kotlin.math.roundToInt

class InMemoryTimeEntriesRepository(initialState: List<TimeEntry> = emptyList()) : TimeEntriesRepository {

    private val state = mutableListOf<TimeEntry>()

    init {
        state.addAll(initialState)
    }

    override suspend fun saveTimeEntries(timeEntries: List<TimeEntry>) {
        state.addAll(timeEntries)
    }

    override suspend fun getHoursByDeveloperAndProject(range: ClosedRange<Instant>): Map<Pair<Developer, Project>, Hours> =
        state.filterIsIn(range)
            .groupBy({ it.developer to it.project }) { it.duration }
            .mapValues { Hours(it.value.sum().inWholeHours.toInt()) }

    private fun List<TimeEntry>.filterIsIn(range: ClosedRange<Instant>) = mapNotNull { timeEntry ->
        range.intersection(timeEntry.range)?.let { timeEntry.copy(range = it) }
    }

    override suspend fun getDeveloperHoursByProjectAndDate(
        developer: Developer,
        dateRange: ClosedRange<LocalDate>
    ): List<Triple<LocalDate, Project, Hours>> =
        state
            .filter { it.developer == developer }
            .filterIsIn(dateRange.toInstantRange())
            .groupBy({ it.localDate to it.project }) { it.duration }
            .mapValues { Hours(((it.value.sum().inWholeSeconds) / 3600.0).roundToInt()) }
            .map { (k, v) -> Triple(k.first, k.second, v) }

    override suspend fun listTimeEntries(timeRange: ClosedRange<Instant>, developer: Developer?): List<TimeEntry>  =
        state
            .filter { timeEntry -> developer?.let { it == timeEntry.developer } ?: true }
            .filterIsIn(timeRange)

    fun getState(): List<TimeEntry> = state.toList()
}
