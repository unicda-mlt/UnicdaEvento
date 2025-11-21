package com.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.main.unicdaevento.MyAppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarSimpleBack (
    navController: NavHostController,
    title: String?,
    showBack: Boolean = true,
) {
    // 1) Detect if we're on the start destination of this graph
    val backEntry by navController.currentBackStackEntryAsState()
    val isOnStartDestination = remember(backEntry) {
        val startId = navController.graph.findStartDestination().id
        backEntry?.destination
            ?.hierarchy
            ?.any { it.id == startId } == true
    }

    var hadPrevious by rememberSaveable { mutableStateOf(navController.previousBackStackEntry != null) }
    LaunchedEffect (navController) {
        snapshotFlow { navController.previousBackStackEntry != null }
            .collect { hasPrev ->
                if (hasPrev) {
                    hadPrevious = true
                }
            }
    }

    val localShowBack = hadPrevious && !isOnStartDestination

    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = title ?: "",
                    fontWeight = FontWeight(700),
                    color = MaterialTheme.colorScheme.surfaceTint
                )
            },
            navigationIcon = {
                if (localShowBack && showBack) {
                    IconButton (onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            }
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.secondaryContainer
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun TopBarSimpleBack_Preview() {
    MyAppTheme {
        Box(modifier = Modifier.padding(10.dp)) {
            TopBarSimpleBack(navController = rememberNavController(), title = "Test")
        }
    }
}

