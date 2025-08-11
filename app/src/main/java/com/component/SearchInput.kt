package com.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.main.unicdaevento.MyAppTheme

@Composable
fun SearchInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    TextField(
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
        interactionSource = interactionSource,
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        prefix = {
            Icon(Icons.Filled.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.secondary)
        },
        shape = RoundedCornerShape(12.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchInput_Preview() {
    MyAppTheme {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            SearchInput (
                value = "",
                onValueChange = {}
            )
        }
    }
}