package com.flow.cms.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.flow.cms.route.CMSFlowRoute
import com.presentation.common.TopBarSimpleBack


@Composable
fun CMSDefaultFlowScaffold(
    navHostController: NavHostController,
    route: String,
    content: @Composable () -> Unit
) {
    Scaffold (
        topBar = {
            TopBarSimpleBack(
                navHostController,
                title = titleFromRoute(titleFromRoute(route))
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}

private fun titleFromRoute(route: String): String {
    return CMSFlowRoute.fromRoute(route)?.title ?: route
}

