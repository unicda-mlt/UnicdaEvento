package com.util

import java.time.format.DateTimeFormatter


val formatterMeridiemTime: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
val formatterMonthDayTime: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d • h:mm a", )
val formatterMonthDay: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d", )
val formatterDayName: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE")

fun formatEpochLongToMeridiemTime(value: Long): String {
    val result = longToEpochLongToLocalTime(value)
    return formatterMeridiemTime.format(result)
}

fun formatEpochLongToMonthDayTime(value: Long): String {
    val result = longToEpochLongToLocalDateTime(value)
    return formatterMonthDayTime.format(result)
}

fun formatEpochLongToMonthDay(value: Long): String {
    val result = longToEpochLongToLocalDateTime(value)
    return formatterMonthDay.format(result)
}

fun formatEpochLongToDayName(value: Long): String {
    val result = longToEpochLongToLocalDateTime(value)
    return formatterDayName.format(result)
}

