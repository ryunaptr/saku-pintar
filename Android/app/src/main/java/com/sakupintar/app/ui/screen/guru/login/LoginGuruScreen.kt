package com.sakupintar.app.ui.screen.guru.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sakupintar.app.ui.component.formFieldColors
import com.sakupintar.app.ui.navigation.Routes
import com.sakupintar.app.ui.theme.ColorAccentContainer
import com.sakupintar.app.ui.theme.ColorBackground
import com.sakupintar.app.ui.theme.ColorDisabled
import com.sakupintar.app.ui.theme.ColorOnAccentContainer
import com.sakupintar.app.ui.theme.ColorPrimary
import com.sakupintar.app.ui.theme.ColorSurface
import com.sakupintar.app.ui.theme.ColorTextPrimary
import com.sakupintar.app.ui.theme.ColorTextSecondary
import com.sakupintar.app.ui.theme.SakuPintarTheme

@Composable
fun LoginGuruScreen(
    navController: NavHostController,
    viewModel: LoginGuruViewModel = viewModel()
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    //Navigasi saat login sukses
    LaunchedEffect(formState.state) {
        if (formState.state is LoginGuruUiState.Success) {
            navController.navigate(Routes.GURU_BERANDA) {
                popUpTo(Routes.HOME) { inclusive = false }
            }
        }
    }

    SakuPintarTheme(isGuruMode = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorBackground)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Selamat Datang",
                style = MaterialTheme.typography.displayLarge,
                color = ColorTextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Masuk sebagai guru",
                style = MaterialTheme.typography.bodyLarge,
                color = ColorTextSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            //Username
            OutlinedTextField(
                value = formState.username,
                onValueChange = { viewModel.setUsername(it) },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            //Password
            OutlinedTextField(
                value = formState.password,
                onValueChange = { viewModel.setPassword(it) },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors(),
                visualTransformation = if (formState.showPassword)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { viewModel.togglePassword() }) {
                        Icon(
                            imageVector = if (formState.showPassword)
                                Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = ColorTextSecondary
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            //Pesan error
            if (formState.state is LoginGuruUiState.Error) {
                val pesan = (formState.state as LoginGuruUiState.Error).pesan
                Text(
                    text = pesan,
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorOnAccentContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ColorAccentContainer, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            //Tombol login
            val isLoading = formState.state is LoginGuruUiState.Loading

            Button(
                onClick = { viewModel.login() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorPrimary,
                    disabledContainerColor = ColorDisabled
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = ColorSurface,
                        strokeWidth = 2.dp,
                        modifier = Modifier.height(24.dp)
                    )
                } else {
                    Text(
                        text = "Masuk",
                        style = MaterialTheme.typography.titleMedium,
                        color = ColorSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}