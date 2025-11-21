package com.main.unicdaevento.screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.component.LoadingOverlay
import com.component.PrimaryButton
import com.component.PrimaryInputSecret
import com.component.PrimaryInputText
import com.example.unicdaevento.R
import com.route.AppNestedRoute

@Composable
fun LoginScreen(
    navController: NavHostController,
    vm: LoginScreenViewModel = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = remember(context) { context as? Activity }
    val isLoading = uiState is LoginScreenViewModel.UiState.Loading

    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginScreenViewModel.UiState.Error -> {
                Toast.makeText(
                    context,
                    (uiState as LoginScreenViewModel.UiState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
            }
            is LoginScreenViewModel.UiState.EmailResetPasswordSent -> {
                Toast.makeText(
                    context,
                    "Se ha enviado un correo a su correo eléctronico para restablecer la contraseña",
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {}
        }
    }

    LoadingOverlay(isLoading)

    if (vm.currentUserOrNull() !== null) {
        navController.navigate(AppNestedRoute.StudentFlow.route) {
            popUpTo(AppNestedRoute.Main.route) { inclusive = true }
            launchSingleTop = true
        }
    }
    else {
        ScreenContent(
            vm = vm,
            isLoading = isLoading,
            onEmailChange = vm::setEmail,
            onPasswordChange = vm::setPassword,
            onSignInEmail = vm::signInEmail,
            onSignUpEmail = vm::signUpEmail,
            onSignInGoogle = {
                if (activity != null) {
                    vm.signInGoogle(activity)
                }
            },
            onSendEmailPasswordReset = vm::sendPasswordReset
        )
    }
}

@Composable
private fun ScreenContent (
    vm: LoginScreenViewModel,
    isLoading: Boolean,
    onEmailChange: (value: String) -> Unit,
    onPasswordChange: (value: String) -> Unit,
    onSignInEmail: (email: String, password: String) -> Unit,
    onSignUpEmail: (email: String, password: String) -> Unit,
    onSignInGoogle: () -> Unit,
    onSendEmailPasswordReset: (email: String) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Column (
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.85f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Logo()

                Spacer(Modifier.height(50.dp))

                Column (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    val email by vm.email.collectAsStateWithLifecycle()
                    val password by vm.password.collectAsStateWithLifecycle()

                    EmailField(value = email, onValueChange = onEmailChange, enabled = !isLoading)

                    Spacer(Modifier.height(10.dp))

                    PasswordField(value = password, onValueChange = onPasswordChange, enabled = !isLoading)

                    Spacer(Modifier.height(30.dp))

                    ScreenButton(
                        text = "Ingresar",
                        onClick = { onSignInEmail(email, password) },
                        enabled = !isLoading
                    )

                    Spacer(Modifier.height(10.dp))

                    ScreenButton(
                        text = "Registrate",
                        onClick = { onSignUpEmail(email, password) },
                        enabled = !isLoading
                    )

                    Spacer(Modifier.height(10.dp))

                    PrimaryButton(
                        onClick = onSignInGoogle,
                        modifier = Modifier.fillMaxWidth(0.8f)
                            .padding(vertical = 0.dp),
                        enabled = !isLoading
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.google),
                            contentDescription = "Google",
                            modifier = Modifier.fillMaxWidth()
                            .aspectRatio( 5f)
                            .clipToBounds()
                                .padding(0.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.fillMaxHeight(0.6f))

            Column (
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()

                val normalColor = MaterialTheme.colorScheme.primary
                val pressedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)

                Text(
                    text = "Restablecer contraseña",
                    color = if (isPressed) pressedColor else normalColor,
                    style = MaterialTheme.typography.labelLarge.copy(
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            onSendEmailPasswordReset(vm.email.value)
                        }
                        .padding(4.dp)
                )
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
private fun EmailField(
    value: String,
    onValueChange: (value: String) -> Unit,
    enabled: Boolean = true
) {
    PrimaryInputText(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text("Correo eléctronico")
        },
        enabled = enabled,
    )
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (value: String) -> Unit,
    enabled: Boolean = true
) {
    PrimaryInputSecret(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text("Contraseña")
        },
        enabled = enabled,
    )
}

@Composable
private fun ScreenButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean
) {
    PrimaryButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(0.8f),
        contentPadding = PaddingValues(
            vertical = 15.dp
        ),
        enabled = enabled
    ) {
        Text(
            text = text,
            fontSize = 24.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreen_Preview() {
    _root_ide_package_.com.main.unicdaevento.MyAppTheme {
        ScreenContent(
            vm = viewModel(),
            isLoading = false,
            onEmailChange = { },
            onPasswordChange = { },
            onSignInEmail = { _, _ -> },
            onSignUpEmail = { _, _ -> },
            onSignInGoogle = { },
            onSendEmailPasswordReset = { }
        )
    }
}