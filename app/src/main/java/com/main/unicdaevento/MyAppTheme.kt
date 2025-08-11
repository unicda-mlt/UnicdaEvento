package com.main.unicdaevento

import androidx.compose.runtime.Composable
import com.ui.theme.AppTheme

@Composable
fun MyAppTheme (content: @Composable (() -> Unit)) {
    AppTheme (
        darkTheme = false,
        dynamicColor = false
    ) {
        content()
    }
}