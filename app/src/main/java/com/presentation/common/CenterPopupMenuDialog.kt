package com.presentation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.window.Dialog


data class MenuOption<T>(
    val value: T,
    val label: String,
    val isDestructive: Boolean = false,
    val icon: ImageVector? = null
)

@Composable
fun <T> CenterPopupMenuDialog(
    visible: Boolean,
    options: List<MenuOption<T>>,
    onDismiss: () -> Unit,
    onSuccess: (T) -> Unit,
    title: String? = null,
    autoDismissOnSelect: Boolean = true
) {
    if (!visible) return

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier
                        .widthIn(min = 220.dp, max = 320.dp)
                        .wrapContentHeight()
                        .clickable(
                            enabled = false,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { }
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        if (!title.isNullOrBlank()) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )

                            HorizontalDivider(
                                Modifier,
                                DividerDefaults.Thickness,
                                DividerDefaults.color
                            )
                        }

                        options.forEachIndexed { idx, opt ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSuccess(opt.value)
                                        if (autoDismissOnSelect) onDismiss()
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (opt.icon != null) {
                                    Icon(
                                        imageVector = opt.icon,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .padding(end = 12.dp),
                                        tint = if (opt.isDestructive)
                                            MaterialTheme.colorScheme.error
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = opt.label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (opt.isDestructive)
                                        MaterialTheme.colorScheme.error
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (idx != options.lastIndex) {
                                HorizontalDivider(
                                    Modifier,
                                    DividerDefaults.Thickness,
                                    DividerDefaults.color
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
