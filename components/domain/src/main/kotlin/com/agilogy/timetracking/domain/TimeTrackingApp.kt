package com.agilogy.timetracking.domain

import arrow.core.Tuple4
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

interface TimeTrackingApp {

    suspend fun saveTimeEntries(developer: DeveloperName, timeEntries: List<Pair<ProjectName, ClosedRange<Instant>>>)
    suspend fun getDeveloperHours(range: ClosedRange<Instant>): Map<Pair<DeveloperName, ProjectName>, Hours>
    suspend fun getDeveloperHoursByProjectAndDate(developer: DeveloperName, dateRange: ClosedRange<LocalDate>):
            List<Triple<LocalDate, ProjectName, Hours>>

    suspend fun listTimeEntries(dateRange: ClosedRange<LocalDate>, developer: DeveloperName?):
            List<Tuple4<DeveloperName, ProjectName, LocalDate, ClosedRange<LocalTime>>>
}

@JvmInline
value class Hours(val value: Int)

