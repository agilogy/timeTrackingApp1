package com.agilogy.timetracking.domain

import com.agilogy.timetracking.drivenadapters.InMemoryTimeEntriesRepository
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
            val developerTimeEntries = listOf(DeveloperTimeEntry(project, start..now))
            app.saveTimeEntries(developer, developerTimeEntries)
            val expected = listOf(TimeEntry(developer, project, start..now))
            assertEquals(expected, timeEntriesRepository.getState())

        }

        test("Get hours per developer") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository(listOf(TimeEntry(developer, project, start..now)))
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val result = app.getDeveloperHours(start..now)
            val expected = mapOf(DeveloperProject(developer, project) to Hours(hours))
            assertEquals(expected, result)
        }

        xtest("Get hours per developer when range is inside the developer hours") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository(listOf(TimeEntry(developer, project, start..now)))
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val result = app.getDeveloperHours(start.plusSeconds(900)..now.minusSeconds(900))
            val expected = mapOf(DeveloperProject(developer, project) to Hours(1))
            assertEquals(expected, result)
        }
        test("Get hours per developer when range is bigger than the developer hours") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository(listOf(TimeEntry(developer, project, start..now)))
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val result = app.getDeveloperHours(start.minusSeconds(7200L)..now.plusSeconds(7200L))
            val expected = mapOf(DeveloperProject(developer, project) to Hours(1))
            assertEquals(expected, result)
        }
        xtest("Get hours per developer when range is outside the developer hours") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository(listOf(TimeEntry(developer, project, start..now)))
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val resultLeft = app.getDeveloperHours(start.minusSeconds(3600L)..now.minusSeconds(7200L))
            val resultRight = app.getDeveloperHours(start.plusSeconds(7200L)..now.plusSeconds(3600L))
            val expected = mapOf(DeveloperProject(developer, project) to Hours(0))
            assertEquals(expected, resultLeft)
            assertEquals(expected, resultRight)
        }
        xtest("Get hours per developer when range makes no sense") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository(listOf(TimeEntry(developer, project, start..now)))
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val resultOutside = app.getDeveloperHours(start.plusSeconds(7200L)..now.minusSeconds(7200L))
            val resultInside = app.getDeveloperHours(start.plusSeconds(2700L)..now.minusSeconds(2700L))
            val expected = emptyMap<DeveloperProject, Hours>()
            assertEquals(expected, resultOutside)
            assertEquals(expected, resultInside)
        }
        xtest("Get hours per developer when only one part of the range is inside") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository(listOf(TimeEntry(developer, project, start..now)))
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val resultStartInsideEndOutside = app.getDeveloperHours(start.plusSeconds(1600L)..now.plusSeconds(1600L))
            val resultStartOutsideEndInside = app.getDeveloperHours(start.minusSeconds(1600L)..now.minusSeconds(1600L))
            val expected = mapOf(DeveloperProject(developer, project) to Hours(1))
            assertEquals(expected, resultStartInsideEndOutside)
            assertEquals(expected, resultStartOutsideEndInside)
        }
    }
}