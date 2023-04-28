package com.agilogy.timetracking.domain

import com.agilogy.time.localDate
import java.time.Instant
import java.time.LocalDate
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

data class TimeEntry(val developer: DeveloperName, val project: ProjectName, val range: ClosedRange<Instant>) {
    val duration: Duration = java.time.Duration.between(range.start, range.endInclusive.plusNanos(1)).toKotlinDuration()
    val localDate: LocalDate by lazy { range.start.localDate() }
}

@JvmInline
value class DeveloperName(val name: String)

@JvmInline
value class ProjectName(val name: String)