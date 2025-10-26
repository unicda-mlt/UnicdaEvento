package com.main.unicdaevento

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.component.LoadingIcon
import com.component.PrimaryButton
import com.component.PrimaryInputSecret
import com.component.PrimaryInputText
import com.database.dao.StudentDao
import com.domain.viewmodel.LoginScreenViewModel
import com.example.unicdaevento.R
import com.route.AppNestedRoute

@Composable
fun LoginScreen(
    navController: NavHostController,
    vm: LoginScreenViewModel = viewModel(),
    studentDao: StudentDao? = null
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    if (state.success && state.error.isNullOrEmpty()) {
        navController.navigate(AppNestedRoute.StudentFlow.route) {
            popUpTo(AppNestedRoute.Main.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box (
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f),
            contentAlignment = Alignment.Center,
        ){
            Column (
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Logo()

                Spacer(Modifier.height(50.dp))

                RegistrationIdField(vm, !state.loading)

                Spacer(Modifier.height(10.dp))

                PasswordField(vm, !state.loading)

                Spacer(Modifier.height(30.dp))

                PrimaryButton(
                    onClick = {
                        if (studentDao != null) {
                            vm.login(studentDao)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    contentPadding = PaddingValues(
                        vertical = 15.dp
                    ),
                    enabled = !state.loading
                ) {
                    if (state.loading) {
                        LoadingIcon(
                            modifier = Modifier.size(33.dp)
                        )
                    }
                    else {
                        Text(
                            text = "Ingresar",
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Logo() {
    Image(
        painter = painterResource(id = R.drawable.logo_icon),
        contentDescription = "UNICDA LOGO",
        modifier = Modifier.size(
            width = 135.dp,
            height = 100.dp
        ),
        contentScale = ContentScale.FillBounds
    )
}

@Composable
private fun RegistrationIdField(vm: LoginScreenViewModel, enabled: Boolean = true) {
    val studentRegistrationId by vm.studentRegistrationId.collectAsStateWithLifecycle()

    PrimaryInputText(
        value = studentRegistrationId,
        onValueChange = vm::setStudentRegistrationId,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text("Matrícula con guiones")
        },
        enabled = enabled,
    )
}

@Composable
private fun PasswordField(vm: LoginScreenViewModel, enabled: Boolean = true) {
    val password by vm.password.collectAsStateWithLifecycle()

    PrimaryInputSecret(
        value = password,
        onValueChange = vm::setPassword,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text("Contraseña")
        },
        enabled = enabled,
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginScreen_Preview() {
    MyAppTheme {
        LoginScreen(
            navController = rememberNavController()
        )
    }
}