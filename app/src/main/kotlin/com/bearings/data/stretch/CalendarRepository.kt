package com.bearings.data.stretch

import java.time.Instant

interface CalendarRepository {
    suspend fun getNextEvent(after: Instant = Instant.now()): CalendarEvent?
}

class MockCalendarRepository : CalendarRepository {
    override suspend fun getNextEvent(after: Instant): CalendarEvent = CalendarEvent(
        title = "Hotel Check-in",
        startTime = Instant.now().plusSeconds(72 * 60),
        location = "Hotel Aurora, Centro",
    )
}
