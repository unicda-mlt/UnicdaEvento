package com.presentation.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.presentation.theme.MyAppTheme

@Composable
fun PrimaryLightButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    Button (
        onClick = onClick,
        modifier = modifier,
        enabled,
        shape,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryLight_Preview() {
    MyAppTheme {
        PrimaryLightButton (
            onClick = {}
        ) {
            Text(
                text = "Demo Button",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}