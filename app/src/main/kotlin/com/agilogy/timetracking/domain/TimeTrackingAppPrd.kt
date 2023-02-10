package com.agilogy.timetracking.domain

import java.time.Instant

class TimeTrackingAppPrd(private val timeEntriesRepository: TimeEntriesRepository) : TimeTrackingApp {

    override suspend fun saveTimeEntries(developer: String, timeEntries: List<DeveloperTimeEntry>) {
        timeEntriesRepository.saveTimeEntries(timeEntries.map { TimeEntry(developer, it.project, it.start, it.end) })
    }

    override suspend fun getDeveloperHours(start: Instant, end: Instant): Map<DeveloperProject, Hours> =
            timeEntriesRepository.getDeveloperHours(start, end)
}