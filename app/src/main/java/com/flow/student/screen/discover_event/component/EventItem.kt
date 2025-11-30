package com.flow.student.screen.discover_event.component

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.presentation.common.CustomAsyncImage
import com.presentation.theme.MyAppTheme
import com.util.formatEpochLongToDayName
import com.util.formatEpochLongToMeridiemTime
import com.util.formatEpochLongToMonthDay
import com.util.formatEpochLongToMonthDayTime
import com.util.localDateTimeToEpochTime
import com.util.longDiffDaysCalendarAware
import java.time.LocalDateTime


@Composable
fun EventItem(
    startDate: Long,
    endDate: Long,
    title: String,
    principalImageUrl: String? = null,
    onClick: () -> Unit = { },
    onLongClick: () -> Unit = { }
) {
    val themePrimaryColor = MaterialTheme.colorScheme.primary

    val textStyleUpThinBlue = remember(themePrimaryColor) {
        TextStyle(
            color = themePrimaryColor,
            fontWeight = FontWeight.W300,
            fontSize = 18.sp
        )
    }

    val textStyleBottomThinBlue = remember(themePrimaryColor) {
        TextStyle(
            color = themePrimaryColor,
            fontWeight = FontWeight(300),
            fontSize = 16.sp
        )
    }

    val diffDays = remember(startDate, endDate) { longDiffDaysCalendarAware(startDate, endDate) }

    val onDayText = remember(startDate) {
        if (diffDays > 7)
            formatEpochLongToMonthDay(startDate)
        else
            formatEpochLongToDayName(startDate)
    }

    val startTimeText = remember(startDate) {
        formatEpochLongToMeridiemTime(startDate)
    }

    val endTimeText = remember(endDate) {
        if (diffDays >= 1)
            formatEpochLongToMonthDayTime(endDate)
        else
            formatEpochLongToMeridiemTime(endDate)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                role = Role.Button
            )
            .padding(
                horizontal = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.55f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(onDayText, style = textStyleUpThinBlue)

            Text(
                title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    color = Color.DarkGray,
                    fontWeight = FontWeight(700),
                    fontSize = 22.sp
                )
            )

            Row (
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    startTimeText,
                    style = textStyleBottomThinBlue
                )

                Text(
                    "-",
                    style = textStyleBottomThinBlue
                )

                Text(endTimeText,
                    style = textStyleBottomThinBlue
                )
            }
        }

        CustomAsyncImage(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(700f / 400f)
                .clip(shape = RoundedCornerShape(16.dp)),
            imageUrl = principalImageUrl,
            width = 700.dp,
            height = 400.dp
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun EventoItem_Preview() {
    val startDate = LocalDateTime.of(2025, 8, 1,10, 10, 0, 0)
    val endDate = LocalDateTime.of(2025, 8, 2,13, 50, 0, 0)

    MyAppTheme {
        Box(modifier = Modifier.padding(10.dp)) {
            EventItem(
                startDate = localDateTimeToEpochTime(startDate),
                endDate = localDateTimeToEpochTime(endDate),
                title = "Event detail super details",
            )
        }
    }
}