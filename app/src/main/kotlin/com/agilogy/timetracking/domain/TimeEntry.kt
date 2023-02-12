package com.agilogy.timetracking.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

data class TimeEntry(val developer: String, val project: String, val range: ClosedRange<Instant>) {
    val duration: Duration = java.time.Duration.between(range.start, range.endInclusive.plusNanos(1)).toKotlinDuration()
    val localDate: LocalDate by lazy { range.start.atZone(ZoneOffset.systemDefault()).toLocalDate() }
}
