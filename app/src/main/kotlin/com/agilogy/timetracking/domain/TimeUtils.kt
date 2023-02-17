package com.agilogy.timetracking.domain

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.time.Duration

fun LocalDateTime.toLocalInstant() = atZone(ZoneOffset.systemDefault()).toInstant()
fun LocalDate.toLocalInstant() = atTime(0, 0).toLocalInstant()

fun  ClosedRange<LocalDate>.toInstantRange(): ClosedRange<Instant> =
    start.toLocalInstant() .. endInclusive.plusDays(1).toLocalInstant()

infix fun ClosedRange<Instant>.intersection(other: ClosedRange<Instant>): ClosedRange<Instant>? {
    val s = max(this.start, other.start)
    val e = min(this.endInclusive, other.endInclusive)
    return if(e > s) (s .. e) else null
}

fun <A : Comparable<A>> max(a: A, b: A) = if (a > b) a else b
fun <A : Comparable<A>> min(a: A, b: A) = if (a <= b) a else b

fun Iterable<Duration>.sum(): Duration = fold(Duration.ZERO) { acc, d -> acc + d }