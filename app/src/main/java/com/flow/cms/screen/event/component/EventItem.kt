package com.flow.cms.screen.event.component

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
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
import com.util.formatEpochLongToFullYearMonthDayTime
import com.util.localDateTimeToEpochTime
import java.time.LocalDateTime


@Composable
fun EventItem(
    startDate: Long,
    endDate: Long,
    title: String,
    principalImageUrl: String? = null,
    onClick: () -> Unit = { }
) {
    val themePrimaryColor = MaterialTheme.colorScheme.primary

    val textStyleBottomThinBlue = remember(themePrimaryColor) {
        TextStyle(
            color = themePrimaryColor,
            fontWeight = FontWeight(300),
            fontSize = 16.sp
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .combinedClickable(
                onClick = onClick,
                role = Role.Button
            )
            .padding(
                horizontal = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.7f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
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

            Column (
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Start",
                        tint = themePrimaryColor
                    )

                    Text(
                        formatEpochLongToFullYearMonthDayTime(startDate),
                        style = textStyleBottomThinBlue
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stop,
                        contentDescription = "End",
                        tint = themePrimaryColor
                    )

                    Text(
                        formatEpochLongToFullYearMonthDayTime(endDate),
                        style = textStyleBottomThinBlue
                    )
                }
            }
        }

        CustomAsyncImage(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(shape = RoundedCornerShape(16.dp)),
            imageUrl = principalImageUrl,
            width = 400.dp,
            height = 300.dp
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