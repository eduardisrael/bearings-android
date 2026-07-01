package com.bearings.data.stretch

import java.time.Instant

data class CalendarEvent(
    val title: String,
    val startTime: Instant,
    val location: String?,
)
