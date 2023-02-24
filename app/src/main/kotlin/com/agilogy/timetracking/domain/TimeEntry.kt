package com.agilogy.timetracking.domain

import com.agilogy.time.localDate
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

data class TimeEntry(val developer: Developer, val project: Project, val range: ClosedRange<Instant>) {
    val duration: Duration = java.time.Duration.between(range.start, range.endInclusive.plusNanos(1)).toKotlinDuration()
    val localDate: LocalDate by lazy { range.start.localDate() }
}



@JvmInline
value class Developer(val name: String)

@JvmInline
value class Project(val name: String)