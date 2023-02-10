package com.agilogy.timetracking

import com.agilogy.timetracking.domain.TimeEntriesRepository
import com.agilogy.timetracking.domain.TimeEntry

class InMemoryTimeEntriesRepository: TimeEntriesRepository {
    private val state = mutableListOf<TimeEntry>()
    override fun saveTimeEntries(timeEntries: List<TimeEntry>) {
        state.addAll(timeEntries)
    }

    fun getState(): List<TimeEntry> = state.toList()

}
