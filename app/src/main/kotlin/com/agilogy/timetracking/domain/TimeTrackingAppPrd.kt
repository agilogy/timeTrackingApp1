package com.agilogy.timetracking.domain

import java.time.Instant

class TimeTrackingAppPrd(private val timeEntriesRepository: TimeEntriesRepository) : TimeTrackingApp {

    override suspend fun saveTimeEntries(developer: String, timeEntries: List<DeveloperTimeEntry>) {
        timeEntriesRepository.saveTimeEntries(timeEntries.map { TimeEntry(developer, it.project, it.range) })
    }

    override suspend fun getDeveloperHours(range: ClosedRange<Instant>): Map<DeveloperProject, Hours> =
            timeEntriesRepository.getHoursByDeveloperAndProject(range)
}