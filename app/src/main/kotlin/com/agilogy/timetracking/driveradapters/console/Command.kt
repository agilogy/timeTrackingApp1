package com.agilogy.timetracking.driveradapters.console

import com.agilogy.timetracking.domain.Developer
import com.agilogy.timetracking.domain.Project
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

sealed interface Command

object Help: Command
data class GlobalReport(val yearMonth: YearMonth): Command
data class DeveloperReport(val yearMonth: YearMonth, val developer: Developer): Command
data class ListTimeEntries(val yearMonth: YearMonth, val developer: Developer?): Command
data class AddTimeEntry(val developer: Developer, val project: Project, val range: ClosedRange<Instant>): Command