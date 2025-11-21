package com.flow.student.scaffold

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import com.presentation.common.TopBarSimpleBack
import com.domain.LocalTopBarController
import com.flow.student.screen.discover_event.DiscoverEventScreen
import com.flow.student.screen.my_event.MyEventScreen
import com.flow.student.screen.setting.SettingScreen
import com.flow.student.route.StudentFlowRoute
import com.domain.TopBarController
import kotlinx.coroutines.launch


@Composable
fun MainFlowScaffold(
    navHostController: NavHostController
) {
    val controller = remember { TopBarController() }

    val tabs = listOf(
        StudentFlowRoute.DISCOVER_EVENT.route,
        StudentFlowRoute.MY_EVENT.route,
        StudentFlowRoute.SETTING.route
    )

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabs.size }
    )

    val scope = rememberCoroutineScope()
    val saveableHolder = rememberSaveableStateHolder()
    var currentRoute by remember { mutableStateOf(tabs[0]) }
    var activeTabNav by remember { mutableStateOf<NavHostController?>(null) }

    CompositionLocalProvider (LocalTopBarController provides controller) {
        Scaffold (
            topBar = {
                val cfg = controller.config
                if (cfg.visible) {
                    if (cfg.content != null) {
                        cfg.content.invoke()
                    } else {
                        TopBarSimpleBack(
                            navHostController,
                            title = titleFromRoute(currentRoute),
                            showBack = activeTabNav?.previousBackStackEntry != null
                        )
                    }
                }
            },
            bottomBar = {
                NavigationBar {
                    tabs.forEachIndexed { index, route ->
                        val selected = pagerState.currentPage == index
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    scope.launch { pagerState.animateScrollToPage(index) }
                                }
                            },
                            icon = { TabIcon(route) },
                            label = { Text(titleFromRoute(route)) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            HorizontalPager(
                state = pagerState,
                beyondViewportPageCount = tabs.lastIndex,
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .fillMaxSize()
            ) { page ->
                saveableHolder.SaveableStateProvider(key = tabs[page]) {
                    val tabNav: NavHostController = rememberNavController()
                    val navBackStackEntry by tabNav.currentBackStackEntryAsState()

                    LaunchedEffect(pagerState.currentPage, navBackStackEntry?.destination?.route) {
                        if (pagerState.currentPage == page) {
                            activeTabNav = tabNav
                            currentRoute = navBackStackEntry?.destination?.route ?: tabs[page]
                        }
                    }

                    NavHost(
                        navController = tabNav,
                        startDestination = tabs[page],
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(StudentFlowRoute.DISCOVER_EVENT.route) {
                            DiscoverEventScreen(navHostController)
                        }
                        composable(StudentFlowRoute.MY_EVENT.route) {
                            MyEventScreen(navHostController)
                        }
                        composable(StudentFlowRoute.SETTING.route) {
                            SettingScreen(navHostController)
                        }
                    }
                }
            }
        }
    }
}

private fun titleFromRoute(route: String): String {
    return StudentFlowRoute.fromRoute(route)?.title ?: route
}

@Composable
private fun TabIcon(route: String) {
    val icon = when(StudentFlowRoute.fromRoute(route)) {
        StudentFlowRoute.DISCOVER_EVENT -> Icons.Filled.Home
        StudentFlowRoute.MY_EVENT -> Icons.Filled.CalendarMonth
        StudentFlowRoute.SETTING -> Icons.Filled.Settings
        else -> Icons.Filled.Android
    }

    Icon(icon, contentDescription = route)
}
