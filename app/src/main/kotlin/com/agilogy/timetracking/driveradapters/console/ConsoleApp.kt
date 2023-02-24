package com.agilogy.timetracking.driveradapters.console

import arrow.core.raise.effect
import arrow.core.raise.fold
import arrow.fx.coroutines.use
import com.agilogy.db.hikari.HikariCp
import com.agilogy.time.toInstantRange
import com.agilogy.time.toLocalDateRange
import com.agilogy.timetracking.domain.TimeTrackingApp
import com.agilogy.timetracking.domain.TimeTrackingAppPrd
import com.agilogy.timetracking.drivenadapters.PostgresTimeEntriesRepository
import java.time.ZoneOffset

suspend fun main(args: Array<String>): Unit =
        HikariCp.dataSource("jdbc:postgresql://localhost/", "postgres", "postgres").use { dataSource ->
        val timeEntriesRepository = PostgresTimeEntriesRepository(dataSource)
        val timeTrackingApp = TimeTrackingAppPrd(timeEntriesRepository)
        println("Your current zone id is ${ZoneOffset.systemDefault()}")
        ConsoleApp(ArgsParser(), timeTrackingApp, Console()).main(args)
    }

class ConsoleApp(
    private val argsParser: ArgsParser,
    private val timeTrackingApp: TimeTrackingApp,
    private val console: Console,
) {

    suspend fun main(args: Array<String>) =
        effect { runCommand(argsParser.parse(args)) }.fold(
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

            Help -> console.printHelp(argsParser.help())
            is ListTimeEntries ->
                console.printTimeEntries(timeTrackingApp.listTimeEntries(cmd.yearMonth.toLocalDateRange(), cmd.developer))

            is AddTimeEntry -> timeTrackingApp.saveTimeEntries(cmd.developer, listOf(cmd.project to cmd.range))
        }
    }
}