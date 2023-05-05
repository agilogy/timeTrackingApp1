package com.agilogy.timetracking.driveradapters.console

import arrow.core.Tuple4
import com.agilogy.timetracking.domain.DeveloperName
import com.agilogy.timetracking.domain.Hours
import com.agilogy.timetracking.domain.ProjectName
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.max

class Console {

    fun printHelp(message: String): Unit = println(message)

    fun print(report: Map<Pair<DeveloperName, ProjectName>, Hours>) {
        val table = table(
            report.map { (dp, hours) -> listOf(dp.first.name, dp.second.name, hours.value.toString()) },
            "Developer", "Project", "Hours"
        )
        println(table)
    }

    fun print(report: List<Triple<LocalDate, ProjectName, Hours>>) {
        val table = table(
            report.map { (date, project, hours) -> listOf(date.toString(), project.name, hours.value.toString()) },
            "Date", "Project", "Hours"
        )
        println(table)
    }

    fun table(data: List<List<String>>, vararg columns: String): String {
        val columnLengths = columns.mapIndexed { i, header -> max(data.maxOfOrNull { it[i].length } ?: 0, header.length) }
        val separators = columnLengths.map { "-" * it }
        return listOf(columns.toList(), separators, * data.toTypedArray()).joinToString(separator = "\n") { row ->
            row.zip(columnLengths).joinToString(" ") { (value, length) -> value.padEnd(length) }
        }
    }

    private operator fun String.times(n: Int): String = repeat(n)

    fun printTimeEntries(listTimeEntries: List<Tuple4<DeveloperName, ProjectName, LocalDate, ClosedRange<LocalTime>>>): Unit = println(
        table(
            listTimeEntries.map { (developer, project, date, range) ->
                listOf(
                    developer.name,
                    project.name,
                    date.toString(),
                    range.start.toString(),
                    range.endInclusive.toString()
                )
            },
            "Developer", "Project", "Date", "Start", "End"
        )
    )
}