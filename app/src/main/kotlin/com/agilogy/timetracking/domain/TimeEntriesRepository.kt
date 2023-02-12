package com.agilogy.timetracking.domain

import java.time.Instant
import java.time.LocalDate

interface TimeEntriesRepository {

    suspend fun saveTimeEntries(timeEntries: List<TimeEntry>)
    suspend fun getHoursByDeveloperAndProject(range: ClosedRange<Instant>): Map<DeveloperProject, Hours>
    suspend fun getDeveloperHoursByProjectAndDate(
        developer: String,
        dateRange: ClosedRange<LocalDate>,
    ): List<Triple<LocalDate, String, Hours>>
}
