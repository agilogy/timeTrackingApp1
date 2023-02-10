package com.agilogy.timetracking.domain

import java.time.Instant

interface TimeTrackingApp {

    fun saveTimeEntries(developer: String, timeEntries: List<DeveloperTimeEntry>)
}

data class DeveloperTimeEntry(val project: String, val start: Instant, val end: Instant)
