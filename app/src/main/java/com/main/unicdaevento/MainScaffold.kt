package com.main.unicdaevento

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable


@Composable
fun MainScaffold(
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold { innerPadding ->
        content(innerPadding)
    }
}
