package com.flow.cms.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flow.cms.route.CMSFlowRoute
import com.main.unicdaevento.MyAppTheme


@Composable
fun HomeScreen (
    navController: NavHostController,
) {
    return Column (
        modifier = Modifier.fillMaxSize().padding(
            horizontal = 30.dp
        ),
        verticalArrangement = Arrangement.spacedBy(
            space = 14.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HomeButton(
            title = "Departments",
            onClick = {
                navController.navigate(CMSFlowRoute.DEPARTMENT_LIST.route)
            }
        )

        HomeButton(
            title = "Categories",
            onClick = { }
        )

        HomeButton(
            title = "Events",
            onClick = { }
        )
    }
}

@Composable
private fun HomeButton (
    title: String,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            vertical = 14.dp
        ),
        onClick = onClick
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 20.sp
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DiscoverEventScreen_Preview() {
    MyAppTheme {
        HomeScreen(navController = rememberNavController())
    }
}