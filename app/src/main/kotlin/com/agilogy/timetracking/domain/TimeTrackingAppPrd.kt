package com.agilogy.timetracking.domain

class TimeTrackingAppPrd(private val timeEntriesRepository: TimeEntriesRepository) : TimeTrackingApp {

    override fun saveTimeEntries(developer: String, timeEntries: List<DeveloperTimeEntry>) {
        timeEntriesRepository.saveTimeEntries(timeEntries.map { TimeEntry(developer, it.project, it.start, it.end) })
    }
}