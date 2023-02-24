package com.agilogy.timetracking.domain

import arrow.core.Tuple4
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

interface TimeTrackingApp {

    suspend fun saveTimeEntries(developer: Developer, timeEntries: List<Pair<Project, ClosedRange<Instant>>>)
    suspend fun getDeveloperHours(range: ClosedRange<Instant>): Map<Pair<Developer, Project>, Hours>
    suspend fun getDeveloperHoursByProjectAndDate(developer: Developer, dateRange: ClosedRange<LocalDate>):
            List<Triple<LocalDate, Project, Hours>>

    suspend fun listTimeEntries(dateRange: ClosedRange<LocalDate>, developer: Developer?):
            List<Tuple4<Developer, Project, LocalDate, ClosedRange<LocalTime>>>
}

@JvmInline
value class Hours(val value: Int)

