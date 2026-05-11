package com.compassenglish.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compassenglish.presentation.RegisterViewModelFactory
import com.compassenglish.presentation.register.RegisterViewModel
import com.compassenglish.ui.components.GoldButton
import com.compassenglish.ui.components.GoldTextField
import com.compassenglish.ui.theme.*

@Composable
fun RegisterScreen(
    onRegisterSuccess: (userId: Int, username: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    vm: RegisterViewModel = viewModel(factory = RegisterViewModelFactory())
) {
    var username       by remember { mutableStateOf("") }
    var email          by remember { mutableStateOf("") }
    var password       by remember { mutableStateOf("") }
    var confirm        by remember { mutableStateOf("") }
    var evaluationMode by remember { mutableStateOf(false) }
    val state by vm.uiState.collectAsState()

    LaunchedEffect(state.user) {
        state.user?.let { onRegisterSuccess(it.id, it.username) }
    }

    Box(modifier = Modifier.fillMaxSize().background(GoldSurface)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))
            Text("🧭", fontSize = 80.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Text("Crear cuenta", fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold, color = GoldDark)
            Spacer(Modifier.height(28.dp))

            GoldTextField(value = username, onValueChange = { username = it },
                placeholder = "Nombre de usuario", leadingIcon = Icons.Default.Person)
            Spacer(Modifier.height(12.dp))

            GoldTextField(value = email, onValueChange = { email = it },
                placeholder = "Correo electrónico", leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
            Spacer(Modifier.height(12.dp))

            GoldTextField(value = password, onValueChange = { password = it },
                placeholder = "Contraseña", leadingIcon = Icons.Default.Lock,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
            Spacer(Modifier.height(12.dp))

            GoldTextField(value = confirm, onValueChange = { confirm = it },
                placeholder = "Confirmar contraseña", leadingIcon = Icons.Default.Lock,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))

            Spacer(Modifier.height(16.dp))

            // Togglea el FSRS
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (evaluationMode)
                        GoldBorder.copy(alpha = 0.3f) else GoldSurfaceCard
                ),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "🧠 Modo Evaluación (FSRS)",
                            color = if (evaluationMode) GoldDark else TextDark,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            "Repaso espaciado adaptativo según tu memoria",
                            color = TextLight,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = evaluationMode,
                        onCheckedChange = { evaluationMode = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor   = GoldSurfaceCard,
                            checkedTrackColor   = GoldDark,
                            uncheckedThumbColor = GoldSurfaceCard,
                            uncheckedTrackColor = GoldBorder
                        )
                    )
                }
            }

            if (state.error.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(state.error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(Modifier.height(24.dp))

            if (state.isLoading) {
                CircularProgressIndicator(color = GoldPrimary)
            } else {
                GoldButton("Crear cuenta", onClick = {
                    val mode = if (evaluationMode) "EVALUATION" else "FREE"
                    vm.register(username, password, confirm, email, mode)
                })
            }

            Spacer(Modifier.height(20.dp))
            Text("¿Ya tienes cuenta? Inicia sesión", color = TextMedium,
                fontWeight = FontWeight.Bold, fontSize = 14.sp,
                modifier = Modifier.clickable { onNavigateToLogin() })
            Spacer(Modifier.height(40.dp))
        }
    }
}