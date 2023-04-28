package com.agilogy.timetracking.domain

import java.time.Instant
import java.time.LocalDate

interface TimeEntriesRepository {

    suspend fun saveTimeEntries(timeEntries: List<TimeEntry>)
    suspend fun getHoursByDeveloperAndProject(range: ClosedRange<Instant>): Map<Pair<Developer, Project>, Hours>
    suspend fun getDeveloperHoursByProjectAndDate(
        developer: Developer,
        dateRange: ClosedRange<LocalDate>,
    ): List<Triple<LocalDate, Project, Hours>>

    suspend fun listTimeEntries(timeRange: ClosedRange<Instant>, developer: Developer?): List<TimeEntry>
}
