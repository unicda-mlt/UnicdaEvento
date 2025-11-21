package com.presentation.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.main.unicdaevento.MyAppTheme

@Composable
fun PrimaryOutlinedInputText(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    OutlinedTextField(
        value,
        onValueChange,
        singleLine = true,
        maxLines = 1,
        modifier = modifier,
        enabled = enabled,
        placeholder = placeholder,
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        interactionSource = interactionSource
    )
}

@Preview(showBackground = true)
@Composable
private fun PrimaryOutlinedInputText_Preview() {
    MyAppTheme {
        Box(
            modifier = Modifier.padding(10.dp)
        ) {
            PrimaryOutlinedInputText (
                value = "",
                onValueChange = {}
            )
        }
    }
}