package com.agilogy.timetracking

import com.agilogy.timetracking.domain.DeveloperTimeEntry
import com.agilogy.timetracking.domain.TimeEntry
import com.agilogy.timetracking.domain.TimeTrackingAppPrd
import io.kotest.core.spec.style.FunSpec
import java.time.Instant

class TimeTrackingAppTest : FunSpec() {
    init {
        test("Save time entries") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository()
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val now = Instant.now()
            val start = now.minusSeconds(3600)
            val developer = "John"
            val project = "Acme Inc."
            val developerTimeEntries = listOf(DeveloperTimeEntry(project, start, now))
            app.saveTimeEntries(developer, developerTimeEntries)
            val expected = listOf(TimeEntry(developer, project, start, now))
            assert(timeEntriesRepository.getState() == expected)

        }
    }
}