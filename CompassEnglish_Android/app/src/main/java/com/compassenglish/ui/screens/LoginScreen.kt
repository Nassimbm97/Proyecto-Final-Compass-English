package com.compassenglish.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compassenglish.presentation.LoginViewModelFactory
import com.compassenglish.presentation.login.LoginViewModel
import com.compassenglish.ui.components.GoldButton
import com.compassenglish.ui.components.GoldTextField
import com.compassenglish.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: (userId: Int, username: String, learningMode: String) -> Unit,
    onNavigateToRegister: () -> Unit,
    vm: LoginViewModel = viewModel(factory = LoginViewModelFactory())
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by vm.uiState.collectAsState()

    // Navega cuando el login tiene éxito
    LaunchedEffect(state.user) {
        state.user?.let { onLoginSuccess(it.id, it.username, it.learningMode) }
    }

    Box(modifier = Modifier.fillMaxSize().background(GoldSurface)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

            Text("Compass", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = GoldDark)
            Text("English", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = GoldPrimary)

            Spacer(Modifier.height(24.dp))
            Text("🧭", fontSize = 90.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(36.dp))

            GoldTextField(value = username, onValueChange = { username = it },
                placeholder = "Usuario", leadingIcon = Icons.Default.Person)

            Spacer(Modifier.height(14.dp))

            GoldTextField(value = password, onValueChange = { password = it },
                placeholder = "Contraseña", leadingIcon = Icons.Default.Lock,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))

            if (state.error.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(state.error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(Modifier.height(24.dp))

            if (state.isLoading) {
                CircularProgressIndicator(color = GoldPrimary)
            } else {
                GoldButton("Iniciar Sesión", onClick = { vm.login(username, password) })
            }

            Spacer(Modifier.height(20.dp))

            Text("Registrarse", color = TextMedium, fontWeight = FontWeight.Bold,
                fontSize = 14.sp, modifier = Modifier.clickable { onNavigateToRegister() })

            Spacer(Modifier.height(40.dp))
        }
    }
}