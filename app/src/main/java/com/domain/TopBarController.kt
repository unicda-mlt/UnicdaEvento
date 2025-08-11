package com.domain

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

@Stable
data class TopBarConfig(
    val visible: Boolean = true,
    val content: (@Composable () -> Unit)? = null
)

/**
 * A controller for managing the state of a dynamic top bar in a single Scaffold.
 *
 * You provide this controller via [LocalTopBarController] so that any screen
 * in your navigation graph can update or hide the top bar.
 *
 * ## How it works:
 * - The top bar configuration is stored as an immutable [TopBarConfig].
 * - When you call [set], it transforms the current config into a new one (immutable copy).
 * - This triggers Compose recomposition in the Scaffold's `topBar` slot.
 * - [clear] resets the top bar to the default configuration.
 *
 * ## Typical usage in a screen:
 *
 * ```kotlin
 * val topBar = LocalTopBarController.current
 *
 * LaunchedEffect(Unit) {
 *     // Hide the top bar
 *     topBar.set { copy(visible = false) }
 *
 *     // Or set a custom top bar
 *     topBar.set {
 *         copy(content = {
 *             CenterAlignedTopAppBar(title = { Text("Title") })
 *         })
 *     }
 * }
 *
 * // Optional
 * DisposableEffect(Unit) { onDispose { topBar.clear() } }
 * ```
 */
class TopBarController {
    var config by mutableStateOf(TopBarConfig())
        private set

    fun update(transform: (TopBarConfig) -> TopBarConfig) {
        config = transform(config)
    }

    fun clear() { config = TopBarConfig() }
}

val LocalTopBarController = staticCompositionLocalOf<TopBarController> {
    error("TopBarController not provided")
}

