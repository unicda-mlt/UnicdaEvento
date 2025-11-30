package com.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.presentation.theme.MyAppTheme
import com.util.formatEpochMonthDayYear


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RangeDatePickerDialog(
    showState: MutableState<Boolean>,
    minDateUtcMillis: Long? = null,
    maxDateUtcMillis: Long? = null,
    initialStartUtcMillis: Long? = null,
    initialEndUtcMillis: Long? = null,
    onDateSelected: (startDateTimeMillis: Long?, endDateTimeMillis: Long?) -> Unit,
    onDismiss: () -> Unit = {},
) {
    if (!showState.value) return

    val selectable = remember(minDateUtcMillis, maxDateUtcMillis) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val afterMin = minDateUtcMillis?.let { utcTimeMillis >= it } ?: true
                val beforeMax = maxDateUtcMillis?.let { utcTimeMillis <= it } ?: true
                return afterMin && beforeMax
            }
        }
    }

    val state = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialStartUtcMillis,
        initialSelectedEndDateMillis = initialEndUtcMillis,
        selectableDates = selectable
    )

    val headlineText by remember(state) {
        derivedStateOf {
            val startDate = state.selectedStartDateMillis
            val endDate = state.selectedEndDateMillis
            when {
                startDate != null && endDate != null -> "${formatEpochMonthDayYear(startDate)} – ${formatEpochMonthDayYear(endDate)}"
                startDate != null -> "${formatEpochMonthDayYear(startDate)} – …"
                else -> "Pick a date range"
            }
        }
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
        confirmButton = {
            val enabled = state.selectedStartDateMillis != null || state.selectedEndDateMillis != null

            TextButton(
                enabled = enabled,
                onClick = {
                    onDateSelected(state.selectedStartDateMillis, state.selectedEndDateMillis)
                    showState.value = false
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = {
                        showState.value = false
                        state.setSelection(null, null)
                        onDateSelected(null, null)
                        onDismiss()
                    }
                ) { Text("Clear") }

                Spacer(Modifier.width(10.dp))

                TextButton(onClick = {
                    showState.value = false
                    onDismiss()
                }) {
                    Text("Cancel")
                }

            }
        }
    ) {
        DateRangePicker(
            state = state,
            showModeToggle = false,
            title = {},
            headline = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        headlineText,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryLight_Preview() {
    val showPicker = remember { mutableStateOf(true) }

    MyAppTheme {
        RangeDatePickerDialog(
            showState = showPicker,
            onDateSelected = { s, e -> }
        )
    }
}