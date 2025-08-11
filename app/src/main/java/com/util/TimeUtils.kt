package com.util

import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

fun longDiffDaysCalendarAware(startMillis: Long, endMillis: Long, zone: ZoneId = SYSTEM_ZONE): Long {
    val startDate = Instant.ofEpochMilli(startMillis).atZone(zone).toLocalDate()
    val endDate = Instant.ofEpochMilli(endMillis).atZone(zone).toLocalDate()
    return ChronoUnit.DAYS.between(startDate, endDate)
}