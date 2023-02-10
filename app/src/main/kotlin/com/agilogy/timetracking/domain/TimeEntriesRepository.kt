package com.agilogy.timetracking.domain

import java.time.Instant

interface TimeEntriesRepository {

    suspend fun saveTimeEntries(timeEntries: List<TimeEntry>)
    suspend fun getDeveloperHours(start: Instant, end: Instant): Map<DeveloperProject, Hours>
}
