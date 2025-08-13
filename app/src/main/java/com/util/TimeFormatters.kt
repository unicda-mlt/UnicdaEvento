package com.util

import java.time.format.DateTimeFormatter


/**
 *      "October 26 · 10:00 AM, 2024"
 */
val formatterFullMonthDayTimeYear: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d · h:mm a, yyyy")

/**
 *      "Aug 11 • 3:45 PM"
 */
val formatterMonthDayTime: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d • h:mm a", )

/**
 *      "Aug 11, 2025"
 */
val formatterMonthDayYear: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", )

/**
 *      "Aug 11"
 */
val formatterMonthDay: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d", )

/**
 *      "Monday"
 */
val formatterDayName: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE")

/**
 *      "3:45 PM"
 */
val formatterMeridiemTime: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")


fun formatEpochLongToFullMonthDayTimeYear(value: Long): String {
    val result = longToEpochLongToLocalDateTime(value)
    return formatterFullMonthDayTimeYear.format(result)
}

fun formatEpochLongToMonthDayTime(value: Long): String {
    val result = longToEpochLongToLocalDateTime(value)
    return formatterMonthDayTime.format(result)
}
fun formatEpochMonthDayYear(value: Long): String {
    val result = longToEpochLongToLocalDateTime(value)
    return formatterMonthDayYear.format(result)
}

fun formatEpochLongToMonthDay(value: Long): String {
    val result = longToEpochLongToLocalDateTime(value)
    return formatterMonthDay.format(result)
}

fun formatEpochLongToDayName(value: Long): String {
    val result = longToEpochLongToLocalDateTime(value)
    return formatterDayName.format(result)
}

fun formatEpochLongToMeridiemTime(value: Long): String {
    val result = longToEpochLongToLocalTime(value)
    return formatterMeridiemTime.format(result)
}