package com.compassenglish.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compassenglish.data.api.RetrofitClient
import com.compassenglish.data.api.UpdateSettingsRequest
import com.compassenglish.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    userId: Int,
    currentUsername: String,
    currentLearningMode: String,
    onBack: () -> Unit,
    onUpdated: (newUsername: String, newLearningMode: String) -> Unit
) {
    var username       by remember { mutableStateOf(currentUsername) }
    var email          by remember { mutableStateOf("") }
    var password       by remember { mutableStateOf("") }
    var confirmPass    by remember { mutableStateOf("") }
    var showPass       by remember { mutableStateOf(false) }
    var showConfirm    by remember { mutableStateOf(false) }
    var evaluationMode by remember { mutableStateOf(currentLearningMode == "EVALUATION") }
    var isSaving       by remember { mutableStateOf(false) }
    var avatarUri      by remember { mutableStateOf<android.net.Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) avatarUri = uri }
    var error          by remember { mutableStateOf("") }
    var success        by remember { mutableStateOf("") }
    val scope          = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GoldSurface)
            .verticalScroll(rememberScrollState())
    ) {
        // ── CABECERA ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(GoldBorder.copy(alpha = 0.45f), GoldSurface)
                    )
                )
                .padding(top = 16.dp, bottom = 24.dp)
        ) {
            // Botón atrás
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(Icons.Default.ArrowBack, null, tint = GoldDark)
            }

            // Avatar + engranaje decorativo
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(12.dp))

                // Avatar con iniciales y borde dorado
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(GoldBorder, GoldDark)
                                )
                            )
                            .border(3.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = username.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp
                        )
                    }
                    // Engranaje dorado sobre el avatar
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(GoldSurfaceCard)
                            .border(2.dp, GoldBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            null,
                            tint = GoldDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    currentUsername,
                    color = TextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    if (evaluationMode) "Modo Evaluación · FSRS activo" else "Modo Libre",
                    color = if (evaluationMode) GoldDark else TextLight,
                    fontSize = 12.sp
                )
            }
        }

        // ── FORMULARIO ───────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            SectionLabel("Datos personales")

            SettingsField(
                value = username,
                onValueChange = { username = it },
                label = "Nombre de usuario",
                icon = Icons.Default.Person
            )

            SettingsField(
                value = email,
                onValueChange = { email = it },
                label = "Email (dejar vacío para no cambiar)",
                icon = Icons.Default.Email
            )

            SectionLabel("Contraseña")

            SettingsField(
                value = password,
                onValueChange = { password = it },
                label = "Nueva contraseña (vacío = no cambiar)",
                icon = Icons.Default.Lock,
                isPassword = true,
                showPassword = showPass,
                onTogglePassword = { showPass = !showPass }
            )

            SettingsField(
                value = confirmPass,
                onValueChange = { confirmPass = it },
                label = "Confirmar contraseña",
                icon = Icons.Default.Lock,
                isPassword = true,
                showPassword = showConfirm,
                onTogglePassword = { showConfirm = !showConfirm }
            )

            SectionLabel("Modo de aprendizaje")

            // Toggle FSRS / Evaluación
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GoldSurfaceCard),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Modo Evaluación",
                                color = TextDark,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            Text(
                                "Activa el algoritmo FSRS para espaciar\nlos repasos según tu memoria real",
                                color = TextLight,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                        Switch(
                            checked = evaluationMode,
                            onCheckedChange = { evaluationMode = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GoldSurfaceCard,
                                checkedTrackColor = GoldDark,
                                uncheckedThumbColor = GoldSurfaceCard,
                                uncheckedTrackColor = GoldBorder
                            )
                        )
                    }

                    if (evaluationMode) {
                        Spacer(Modifier.height(10.dp))
                        HorizontalDivider(color = GoldBorder.copy(alpha = 0.5f))
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FsrsBadge("📅", "Repaso espaciado")
                            FsrsBadge("🧠", "Curva de olvido")
                            FsrsBadge("⚡", "Intervalos adaptativos")
                        }
                    }
                }
            }

            if (error.isNotEmpty()) {
                Text(error, color = Color(0xFFE53935), fontSize = 13.sp)
            }
            if (success.isNotEmpty()) {
                Text(success, color = Color(0xFF43A047), fontSize = 13.sp,
                    fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(4.dp))

            // Botón guardar
            Button(
                onClick = {
                    error = ""
                    success = ""
                    if (password.isNotEmpty() && password != confirmPass) {
                        error = "Las contraseñas no coinciden"
                        return@Button
                    }
                    if (username.isBlank()) {
                        error = "El nombre no puede estar vacío"
                        return@Button
                    }
                    isSaving = true
                    scope.launch {
                        try {
                            val resp = RetrofitClient.api.updateSettings(
                                userId,
                                UpdateSettingsRequest(
                                    username     = username.trim().takeIf { it != currentUsername },
                                    email        = email.trim().takeIf { it.isNotEmpty() },
                                    password     = password.takeIf { it.isNotEmpty() },
                                    avatarUrl    = avatarUri?.toString(),
                                    learningMode = if (evaluationMode) "EVALUATION" else "FREE"
                                )
                            )
                            if (resp.isSuccessful) {
                                success = "Cambios guardados ✓"
                                onUpdated(username.trim(), if (evaluationMode) "EVALUATION" else "FREE")
                            } else {
                                val json = resp.errorBody()?.string() ?: ""
                                error = try {
                                    org.json.JSONObject(json).optString("error", "Error al guardar")
                                } catch (_: Exception) { "Error al guardar" }
                            }
                        } catch (e: Exception) {
                            error = "Error de conexión: ${e.message}"
                        }
                        isSaving = false
                    }
                },
                enabled = !isSaving,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    disabledContainerColor = GoldBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Guardar cambios",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        color = GoldDark,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun SettingsField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        leadingIcon = { Icon(icon, null, tint = GoldPrimary, modifier = Modifier.size(20.dp)) },
        trailingIcon = if (isPassword && onTogglePassword != null) {
            {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        null,
                        tint = TextLight,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !showPassword)
            PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = GoldPrimary,
            unfocusedBorderColor = GoldBorder,
            focusedContainerColor   = GoldSurfaceCard,
            unfocusedContainerColor = GoldSurfaceCard,
            focusedLabelColor    = GoldDark,
            unfocusedLabelColor  = TextLight
        )
    )
}

@Composable
private fun FsrsBadge(emoji: String, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .background(GoldBorder.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(emoji, fontSize = 11.sp)
        Text(label, color = TextMedium, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}