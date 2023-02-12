package com.agilogy.timetracking.drivenadapters

import com.agilogy.timetracking.domain.DeveloperProject
import com.agilogy.timetracking.domain.Hours
import com.agilogy.timetracking.domain.TimeEntriesRepository
import com.agilogy.timetracking.domain.TimeEntry
import com.agilogy.timetracking.domain.intersection
import com.agilogy.timetracking.domain.sum
import com.agilogy.timetracking.domain.toInstantRange
import java.time.Instant
import java.time.LocalDate

class InMemoryTimeEntriesRepository(initialState: List<TimeEntry> = emptyList()) : TimeEntriesRepository {

    private val state = mutableListOf<TimeEntry>()

    init {
        state.addAll(initialState)
    }

    override suspend fun saveTimeEntries(timeEntries: List<TimeEntry>) {
        state.addAll(timeEntries)
    }

    override suspend fun getHoursByDeveloperAndProject(range: ClosedRange<Instant>): Map<DeveloperProject, Hours> =
        state.filterIsIn(range)
            .groupBy({ DeveloperProject(it.developer, it.project) }) { it.duration }
            .mapValues { Hours(it.value.sum().inWholeHours.toInt()) }

    private fun List<TimeEntry>.filterIsIn(range: ClosedRange<Instant>) = mapNotNull { timeEntry ->
        range.intersection(timeEntry.range)?.let { timeEntry.copy(range = it) }
    }

    override suspend fun getDeveloperHoursByProjectAndDate(
        developer: String,
        dateRange: ClosedRange<LocalDate>,
    ): List<Triple<LocalDate, String, Hours>> =
        state
            .filter { it.developer == developer }
            .filterIsIn(dateRange.toInstantRange())
            .groupBy({ it.localDate to it.project }) { it.duration }
            .mapValues { Hours(it.value.sum().inWholeHours.toInt()) }
            .map { (k, v) -> Triple(k.first, k.second, v) }

    fun getState(): List<TimeEntry> = state.toList()
}
