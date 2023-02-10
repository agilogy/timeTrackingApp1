package com.agilogy.timetracking.domain

interface TimeEntriesRepository {

    fun saveTimeEntries(timeEntries: List<TimeEntry>)
}
