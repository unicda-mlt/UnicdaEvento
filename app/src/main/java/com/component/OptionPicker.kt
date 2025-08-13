package com.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.main.unicdaevento.MyAppTheme
import androidx.compose.ui.res.colorResource
import com.example.unicdaevento.R


data class Option<T>(
    val value: T,
    val label: String
)

@Composable
fun <T> OptionPicker(
    modifier: Modifier = Modifier,
    title: String,
    options: List<Option<T>>,
    selected: T? = null,
    onSelect: (T) -> Unit = {},
    enabled: Boolean = true,
    itemContent: (@Composable (Option<T>, Boolean) -> Unit)? = null // optional custom row
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OptionPickerButton(
            modifier = modifier,
            text = title,
            isSelected = selected != null,
            enabled = enabled,
            onClick = {
                expanded = true
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                val isSelected = option.value == selected

                DropdownMenuItem(
                    modifier = Modifier.
                    background(if (isSelected) MaterialTheme.colorScheme.surfaceDim else Color.Transparent),
                    text = {
                        if (itemContent == null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    option.label,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 18.sp
                                )
                            }
                        } else {
                            itemContent(option, isSelected)
                        }
                    },
                    onClick = {
                        onSelect(option.value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun OptionPickerButton (
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    var surfaceColor = MaterialTheme.colorScheme.surface

    if (!enabled) {
        surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    }
    if (isSelected) {
        surfaceColor = MaterialTheme.colorScheme.primary
    }

    Surface(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .clip(MaterialTheme.shapes.medium)
            .combinedClickable(
                enabled = enabled,
                role = Role.Button
            ) { onClick() },
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium,
        color = surfaceColor,
        border = DividerDefaults.color.copy(alpha = 0.3f).let {
            BorderStroke(1.dp, it)
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (!isSelected) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.surface
            )
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = if (!isSelected) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.surface,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OptionPicker_Preview() {
    MyAppTheme {
        Box(
            modifier = Modifier.padding(10.dp)
        ) {
            OptionPicker(
                title = "Categories",
                options = listOf(
                    Option(null, "All"),
                    Option(1, "Opción Número 1"),
                    Option(1, "Opción Número 2"),
                    Option(1, "Opción Número 3")
                ),
                selected = 1,
                onSelect = { v -> {} }
            )
        }
    }
}
