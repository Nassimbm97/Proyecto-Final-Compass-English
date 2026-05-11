package com.compassenglish.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compassenglish.ui.components.GoldButton
import com.compassenglish.ui.theme.*
import java.io.Serializable

// Configuración de sesión que se pasa al ExerciseScreen
data class SessionConfig(
    val theme: String?       = null,   // null = todos los temas
    val exerciseType: String? = null,  // null = aleatorio
    val source: String       = "BD",   // BD, CUSTOM, ALL
    val level: String?       = null    // null = nivel del usuario
) : Serializable

@Composable
fun ExerciseFilterScreen(
    onBack: () -> Unit,
    onStart: (SessionConfig) -> Unit
) {
    var selectedTheme by remember { mutableStateOf<String?>(null) }
    var selectedType  by remember { mutableStateOf<String?>(null) }
    var selectedSource by remember { mutableStateOf("BD") }
    var selectedLevel by remember { mutableStateOf<String?>(null) }

    val themes = listOf(
        null to "🌍 Todos los temas",
        "Sports"    to "🏅 Deportes",
        "Food"      to "🍎 Comida",
        "Countries" to "🌍 Países",
        "Anatomy"   to "🫀 Anatomía",
        "Weather"   to "☀️ Tiempo",
        "Hobbies"   to "🎸 Hobbies",
        "Jobs"      to "💼 Trabajos",
        "Others"    to "📦 Otros"
    )

    val types = listOf(
        null          to "🎲 Aleatorio",
        "TRANSLATE"       to "✏️ Traducción",
        "MULTIPLE_CHOICE" to "🔘 Opción múltiple",
        "FILL_GAP"        to "📝 Fill-the-gap"
    )

    val sources = listOf(
        "BD"     to "📚 Palabras del curso",
        "CUSTOM" to "⭐ Mis tarjetas",
        "ALL"    to "🔀 Todo mezclado"
    )

    val levels = listOf(
        null           to "📊 Mi nivel actual",
        "ALL"          to "🌐 Todos los niveles",
        "BEGINNER"     to "🟢 Básico",
        "INTERMEDIATE" to "🟡 Intermedio",
        "ADVANCED"     to "🔴 Avanzado"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GoldSurface)
    ) {
        // TopBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = GoldDark)
            }
            Text(
                "Configurar sesión",
                color = TextDark,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {

                // Fuente
            FilterSection(title = "¿De dónde quieres practicar?") {
                sources.forEach { (value, label) ->
                    SelectableChip(
                        label = label,
                        selected = selectedSource == value,
                        onClick = { selectedSource = value }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            //  tEma solo si es BD u ALL.
            if (selectedSource != "CUSTOM") {
                FilterSection(title = "Tema") {
                    themes.forEach { (value, label) ->
                        SelectableChip(
                            label = label,
                            selected = selectedTheme == value,
                            onClick = { selectedTheme = value }
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // ---- TIPO DE EJERCICIO ----
            // Fill-the-gap no disponible para tarjetas propias
            val availableTypes = if (selectedSource == "CUSTOM")
                types.filter { it.first != "FILL_GAP" }
            else types

            FilterSection(title = "Tipo de ejercicio") {
                availableTypes.forEach { (value, label) ->
                    SelectableChip(
                        label = label,
                        selected = selectedType == value,
                        onClick = { selectedType = value }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ---- NIVEL ----
            FilterSection(title = "Nivel de dificultad") {
                levels.forEach { (value, label) ->
                    SelectableChip(
                        label = label,
                        selected = selectedLevel == value,
                        onClick = { selectedLevel = value }
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }

        // Botón empezar
        Box(modifier = Modifier.padding(20.dp)) {
            GoldButton(
                text = "Empezar sesión ▶",
                onClick = {
                    onStart(
                        SessionConfig(
                            theme        = selectedTheme,
                            exerciseType = selectedType,
                            source       = selectedSource,
                            level        = selectedLevel
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun FilterSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(title, color = TextMedium, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    Spacer(Modifier.height(10.dp))
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GoldSurfaceCard),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            content = content
        )
    }
}

@Composable
fun SelectableChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (selected) GoldPrimary else GoldSurface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = if (selected) Color.White else TextDark,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            if (selected) {
                Text("✓", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}