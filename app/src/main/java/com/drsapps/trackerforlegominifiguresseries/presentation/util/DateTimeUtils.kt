package com.drsapps.trackerforlegominifiguresseries.presentation.util

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus

// Helps you know if a certain number of days has passed since a given timestamp
fun hasDaysPassedSince(timestamp: Instant, days: Int): Boolean {
    val daysAgoDate = Clock.System.now().minus(days, DateTimeUnit.DAY, TimeZone.UTC)
    return timestamp < daysAgoDate
}