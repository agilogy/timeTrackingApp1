package com.agilogy.timetracking.domain

import arrow.core.Tuple4
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

class TimeTrackingAppPrd(private val timeEntriesRepository: TimeEntriesRepository) : TimeTrackingApp {

    override suspend fun saveTimeEntries(developer: DeveloperName, timeEntries: List<Pair<ProjectName, ClosedRange<Instant>>>) {
        timeEntriesRepository.saveTimeEntries(timeEntries.map { TimeEntry(developer, it.first, it.second) })
    }

    override suspend fun getDeveloperHours(range: ClosedRange<Instant>): Map<Pair<DeveloperName, ProjectName>, Hours> =
        timeEntriesRepository.getHoursByDeveloperAndProject(range)

    override suspend fun getDeveloperHoursByProjectAndDate(developer: DeveloperName, dateRange: ClosedRange<LocalDate>):
            List<Triple<LocalDate, ProjectName, Hours>> =
        timeEntriesRepository.getDeveloperHoursByProjectAndDate(developer, dateRange)

    override suspend fun listTimeEntries(dateRange: ClosedRange<LocalDate>, developer: DeveloperName?):
            List<Tuple4<DeveloperName, ProjectName, LocalDate, ClosedRange<LocalTime>>> {
        val timeEntries = timeEntriesRepository.listTimeEntries(dateRange.toInstantRange(), developer)
        return timeEntries.flatMap { timeEntry ->
            fun row(date: LocalDate, range: ClosedRange<LocalTime>) =
                Tuple4(timeEntry.developer, timeEntry.project, date, range)

            val res = if (timeEntry.range.endInclusive.localDate() != timeEntry.localDate) {
                listOf(
                    row(timeEntry.localDate, timeEntry.range.start.localTime()..LocalTime.of(23, 59, 59)),
                    row(timeEntry.localDate.plusDays(1), LocalTime.of(0, 0)..timeEntry.range.endInclusive.localTime())
                )
            } else {
                listOf(
                    row(timeEntry.localDate, timeEntry.range.start.localTime()..timeEntry.range.endInclusive.localTime())
                )
            }
            res
        }
    }
}