package com.agilogy.timetracking.domain

import java.time.Instant

data class TimeEntry(val developer: String, val project: String, val start: Instant, val end: Instant)
