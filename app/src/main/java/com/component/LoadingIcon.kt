package com.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.main.unicdaevento.MyAppTheme

@Composable
fun LoadingIcon(
    modifier: Modifier = Modifier,
    speedMs: Int = 1300,
    icon: ImageVector = Icons.Default.Cached
) {
    val infinite = rememberInfiniteTransition(label = "spin")

    val angle by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = speedMs, easing = LinearEasing)
        ),
        label = "angle"
    )

    Icon(
        imageVector = icon,
        contentDescription = "Loading",
        modifier = modifier.rotate(angle)
    )
}

@Preview(showBackground = true)
@Composable
private fun LoadingIcon_Preview() {
    MyAppTheme {
        Box(
            modifier = Modifier.padding(10.dp)
        ) {
            LoadingIcon()
        }
    }
}