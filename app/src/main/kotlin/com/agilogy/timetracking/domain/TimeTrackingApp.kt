package com.agilogy.timetracking.domain

import java.time.Instant

interface TimeTrackingApp {

    fun saveTimeEntries(developer: String, timeEntries: List<DeveloperTimeEntry>)
    fun getDeveloperHours(start: Instant, end: Instant): Map<DeveloperProject, Hours>
}

@JvmInline
value class Hours(val value: Int)
data class DeveloperProject(val developer: String, val project: String)
data class DeveloperTimeEntry(val project: String, val start: Instant, val end: Instant)
