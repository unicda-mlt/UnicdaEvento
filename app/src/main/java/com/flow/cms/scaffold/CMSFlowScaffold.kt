package com.flow.cms.scaffold

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.domain.LocalTopBarController
import com.domain.TopBarController
import com.flow.cms.route.CMSFlowRoute
import com.flow.cms.screen.home.HomeScreen
import com.flow.cms.screen.setting.SettingScreen
import com.presentation.common.TopBarSimpleBack
import com.route.AppNestedRoute
import com.route.main.MainAppRoute
import kotlinx.coroutines.launch

@Composable
fun CMSFlowScaffold(
    navHostController: NavHostController,
    vm: CMSFlowScaffoldViewModel = hiltViewModel()
) {
    val currentUser by vm.currentUser.collectAsStateWithLifecycle()
    val controller = remember { TopBarController() }

    val tabs = remember {
        listOf(
            CMSFlowRoute.HOME.route,
            CMSFlowRoute.SETTING.route
        )
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabs.size }
    )

    val scope = rememberCoroutineScope()
    val saveableHolder = rememberSaveableStateHolder()
    var currentRoute by remember { mutableStateOf(tabs[0]) }
    var activeTabNav by remember { mutableStateOf<NavHostController?>(null) }
    var shouldResetPaginationOnInit by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(navHostController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val route = destination.route

            if (shouldResetPaginationOnInit && route == CMSFlowRoute.HOME.route) {
                scope.launch {
                    pagerState.scrollToPage(0)
                    shouldResetPaginationOnInit = false
                }
            }
        }

        navHostController.addOnDestinationChangedListener(listener)

        onDispose {
            navHostController.removeOnDestinationChangedListener(listener)
        }
    }

    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            scope.launch {
                shouldResetPaginationOnInit = true
                navHostController.navigate(AppNestedRoute.Main.route) {
                    popUpTo(MainAppRoute.Login.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

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
                        composable(CMSFlowRoute.HOME.route) {
                            HomeScreen(navHostController)
                        }
                        composable(CMSFlowRoute.SETTING.route) {
                            SettingScreen()
                        }
                    }
                }
            }
        }
    }
}

private fun titleFromRoute(route: String): String {
    return CMSFlowRoute.fromRoute(route)?.title ?: route
}

@Composable
private fun TabIcon(route: String) {
    val icon = when(CMSFlowRoute.fromRoute(route)) {
        CMSFlowRoute.HOME -> Icons.Filled.Home
        CMSFlowRoute.SETTING -> Icons.Filled.Settings
        else -> Icons.Filled.Android
    }

    Icon(icon, contentDescription = route)
}
