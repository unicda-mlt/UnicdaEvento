package com.presentation.common

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    value: Long,
    onValueChange: (Long) -> Unit,
    showState: MutableState<Boolean>
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val systemZone = remember { ZoneId.systemDefault() }

    val initialDateTime = remember(value) {
        Instant.ofEpochMilli(value)
            .atZone(systemZone)
            .toLocalDateTime()
    }

    var tempDate by remember { mutableStateOf(initialDateTime.toLocalDate()) }
    var tempTime by remember { mutableStateOf(initialDateTime.toLocalTime()) }

    // ------------- DATE PICKER -------------
    if (showState.value) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = tempDate
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant().toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showState.value = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis ?: return@TextButton
                    tempDate = Instant.ofEpochMilli(millis)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate()

                    showState.value = false
                    showTimePicker = true
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ------------- TIME PICKER -------------
    if (showTimePicker) {
        val timeState = rememberTimePickerState(
            initialHour = tempTime.hour,
            initialMinute = tempTime.minute
        )

        TimePickerDialog(
            title = { Text("Select time") },
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                    tempTime = LocalTime.of(timeState.hour, timeState.minute)
                    showTimePicker = false

                    val newDateTime = LocalDateTime.of(tempDate, tempTime)
                    val newMillis = newDateTime
                        .atZone(systemZone)
                        .toInstant()
                        .toEpochMilli()

                    onValueChange(newMillis)
                }) {
                    Text("OK")
                }
            }
        ) {
            TimePicker(state = timeState)
        }
    }
}
