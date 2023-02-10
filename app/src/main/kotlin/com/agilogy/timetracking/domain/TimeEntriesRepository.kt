package com.agilogy.timetracking.domain

import java.time.Instant

interface TimeEntriesRepository {

    fun saveTimeEntries(timeEntries: List<TimeEntry>)
    fun getDeveloperHours(start: Instant, end: Instant): Map<DeveloperProject, Hours>
}
