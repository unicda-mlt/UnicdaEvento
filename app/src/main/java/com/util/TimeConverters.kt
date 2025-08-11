package com.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

fun longToEpochLongToLocalTime(value: Long, zone: ZoneId = SYSTEM_ZONE): LocalTime {
    return Instant.ofEpochMilli(value).atZone(zone).toLocalTime()
}

fun longToEpochLongToLocalDateTime(value: Long, zone: ZoneId = SYSTEM_ZONE): LocalDateTime {
    return Instant.ofEpochMilli(value).atZone(zone).toLocalDateTime()
}

fun localDateTimeToEpochTime(localDateTime: LocalDateTime, zone: ZoneId = SYSTEM_ZONE): Long {
    return localDateTime.atZone(zone).toInstant().toEpochMilli()
}
