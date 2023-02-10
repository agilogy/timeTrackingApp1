package com.agilogy.timetracking

import com.agilogy.timetracking.domain.*
import io.kotest.core.spec.style.FunSpec
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.Instant

class TimeTrackingAppTest : FunSpec() {
    init {
        val now = Instant.now()
        val hours = 1
        val start = now.minusSeconds(hours * 3600L)
        val developer = "John"
        val project = "Acme Inc."


        test("Save time entries") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository()
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val developerTimeEntries = listOf(DeveloperTimeEntry(project, start, now))
            app.saveTimeEntries(developer, developerTimeEntries)
            val expected = listOf(TimeEntry(developer, project, start, now))
            assertEquals(expected, timeEntriesRepository.getState())

        }

        test("Get hours per developer"){

            val timeEntriesRepository = InMemoryTimeEntriesRepository(
                    listOf(TimeEntry(developer, project, start, now))
            )
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val result = app.getDeveloperHours(start, now)
            val expected = mapOf(DeveloperProject(developer, project) to Hours(hours))
            assertEquals(expected, result)
        }
    }
}