package com.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex


@Composable
fun LoadingOverlay(
    show: Boolean
) {
    if (!show) return
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = 0.35f))
            .zIndex(1f)
            .pointerInput(Unit) {
                awaitPointerEventScope { while (true) awaitPointerEvent() }
            }
    ) {
        CustomCircularProgressIndicator()
    }
}