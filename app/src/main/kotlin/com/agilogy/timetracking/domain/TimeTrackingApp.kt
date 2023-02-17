package com.agilogy.timetracking.domain

import java.time.Instant

interface TimeTrackingApp {

    suspend fun saveTimeEntries(developer: String, timeEntries: List<DeveloperTimeEntry>)
    suspend fun getDeveloperHours(range: ClosedRange<Instant>): Map<DeveloperProject, Hours>
}

@JvmInline
value class Hours(val value: Int)
data class DeveloperProject(val developer: String, val project: String)
data class DeveloperTimeEntry(val project: String, val range: ClosedRange<Instant>)
