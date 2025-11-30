package com.presentation.theme

import androidx.compose.runtime.Composable

@Composable
fun MyAppTheme (content: @Composable (() -> Unit)) {
    AppTheme (
        darkTheme = false,
        dynamicColor = false
    ) {
        content()
    }
}