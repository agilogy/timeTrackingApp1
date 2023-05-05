package com.agilogy.timetracking.domain

import java.time.Instant
import java.time.LocalDate

interface TimeEntriesRepository {

    suspend fun saveTimeEntries(timeEntries: List<TimeEntry>)
    suspend fun getHoursByDeveloperAndProject(range: ClosedRange<Instant>): Map<Pair<DeveloperName, ProjectName>, Hours>
    suspend fun getDeveloperHoursByProjectAndDate(
        developer: DeveloperName,
        dateRange: ClosedRange<LocalDate>,
    ): List<Triple<LocalDate, ProjectName, Hours>>

    suspend fun listTimeEntries(timeRange: ClosedRange<Instant>, developer: DeveloperName?): List<TimeEntry>
}
