package com.agilogy.timetracking.driveradapters.console

import com.agilogy.timetracking.domain.DeveloperName
import com.agilogy.timetracking.domain.ProjectName
import java.time.Instant
import java.time.YearMonth

sealed interface Command

object Help : Command
data class GlobalReport(val yearMonth: YearMonth) : Command
data class DeveloperReport(val yearMonth: YearMonth, val developer: DeveloperName) : Command
data class ListTimeEntries(val yearMonth: YearMonth, val developer: DeveloperName?) : Command
data class AddTimeEntry(val developer: DeveloperName, val project: ProjectName, val range: ClosedRange<Instant>) : Command