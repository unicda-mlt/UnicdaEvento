package com.component.discover_event

import android.content.Context
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
import com.component.RandomPicsumThumb
import com.example.unicdaevento.R
import com.main.unicdaevento.MyAppTheme
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
    onClick: () -> Unit = { },
) {
    val textStyleUpThinBlue = TextStyle(
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight(300),
        fontSize = 18.sp
    )
    val textStyleBottomThinBlue = TextStyle(
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight(300),
        fontSize = 16.sp
    )

    val diffDays = longDiffDaysCalendarAware(startDate, endDate)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .combinedClickable(
                onClick = { onClick() },
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
            Text(
                if (diffDays > 7)
                    formatEpochLongToMonthDay(startDate)
                else
                    formatEpochLongToDayName(startDate),
                style = textStyleUpThinBlue
            )

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
                    formatEpochLongToMeridiemTime(startDate),
                    style = textStyleBottomThinBlue
                )

                Text(
                    "-",
                    style = textStyleBottomThinBlue
                )

                Text(
                    if (diffDays >= 1)
                        formatEpochLongToMonthDayTime(endDate)
                    else
                        formatEpochLongToMeridiemTime(endDate),
                    style = textStyleBottomThinBlue
                )
            }
        }

        RandomPicsumThumb(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(700f / 400f)
                .clip(shape = RoundedCornerShape(16.dp)),
            widthDp = 700,
            heightDp = 400,
            errorResId = R.drawable.event_dafault
        )
    }
}

fun Context.dpToNearest10Px(dp: Int): Int {
    val px = (dp * resources.displayMetrics.density).toInt()
    return if (px % 10 > 5) {
        ((px / 10) + 1) * 10
    } else {
        (px / 10) * 10
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
                title = "Event detail super details"
            )
        }
    }
}