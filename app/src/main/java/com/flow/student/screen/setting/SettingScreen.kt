package com.flow.student.screen.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.component.LoadingOverlay
import com.auth.AuthViewModel
import com.main.unicdaevento.MyAppTheme
import com.route.AppNestedRoute

@Composable
fun SettingScreen(
    navController: NavHostController,
    vm: AuthViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LoadingOverlay(state is AuthViewModel.UiState.Loading)

    if (state is AuthViewModel.UiState.Idle || vm.currentUserOrNull() == null) {
        navController.navigate(AppNestedRoute.Main.route) {
            popUpTo(AppNestedRoute.Main.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    ScreenContent(
        onSignOut = vm::signOut
    )
}

@Composable
private fun ScreenContent(
    onSignOut: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(0.dp),
        contentAlignment = Alignment.BottomStart,
    ) {

        Button(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 0.dp)
                .height(IntrinsicSize.Min),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black,
            ),
            shape = RoundedCornerShape(0.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 5.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Salir",
                )

                Spacer(Modifier.width(10.dp))

                Text(
                    text = "Salir",
                    fontSize = 30.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingScreen_Preview() {
    MyAppTheme {
        ScreenContent(
            onSignOut = { }
        )
    }
}