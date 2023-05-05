package com.agilogy.timetracking.driveradapters.console

import arrow.core.raise.effect
import arrow.core.raise.fold
import com.agilogy.timetracking.domain.TimeTrackingApp
import com.agilogy.timetracking.domain.toInstantRange
import com.agilogy.timetracking.domain.toLocalDateRange

class ConsoleAdapter(
    private val timeTrackingApp: TimeTrackingApp,
    private val console: Console = Console(),
) {

    suspend fun main(args: Array<String>) =
        effect { runCommand(ArgsParser.parse(args)) }.fold(
            {
                println(it.message)
                runCommand(Help)
            },
            {
                println("Command executed successfully")
            }
        )

    private suspend fun runCommand(cmd: Command) {
        when (cmd) {
            is GlobalReport -> {
                val report = timeTrackingApp.getDeveloperHours(cmd.yearMonth.toInstantRange())
                console.print(report)
            }

            is DeveloperReport -> {
                val report = timeTrackingApp.getDeveloperHoursByProjectAndDate(cmd.developer, cmd.yearMonth.toLocalDateRange())
                console.print(report)
            }

            Help -> console.printHelp(ArgsParser.help())
            is ListTimeEntries ->
                console.printTimeEntries(timeTrackingApp.listTimeEntries(cmd.yearMonth.toLocalDateRange(), cmd.developer))

            is AddTimeEntry -> timeTrackingApp.saveTimeEntries(cmd.developer, listOf(cmd.project to cmd.range))
        }
    }
}