package com.presentation.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.main.unicdaevento.MyAppTheme

@Composable
fun PrimaryInputSecret(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

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
            keyboardType = KeyboardType.Password
        ),
        interactionSource = interactionSource,
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            var icon: ImageVector = Icons.Filled.VisibilityOff
            var description = "Show password"

            if (passwordVisible && enabled) {
                icon = Icons.Filled.Visibility
                description = "Hide password"
            }

            IconButton(
                onClick = { passwordVisible = !passwordVisible },
                enabled = enabled
            ) {
                Icon(imageVector = icon, contentDescription = description)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun PrimaryInputSecret_Preview() {
    MyAppTheme {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            PrimaryInputSecret (
                value = "",
                onValueChange = {}
            )
        }
    }
}