package com.compassenglish.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compassenglish.data.api.HomeStats
import com.compassenglish.data.api.RetrofitClient
import com.compassenglish.ui.components.MenuCard
import com.compassenglish.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    userId: Int,
    username: String,
    points: Int,
    onNavigateToMyCards: () -> Unit,
    onNavigateToExercises: () -> Unit,
    onNavigateToDictionary: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToRanking: () -> Unit,
    onNavigateToFsrs: (state: String) -> Unit
) {
    var stats     by remember { mutableStateOf<HomeStats?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        scope.launch {
            try {
                val resp = RetrofitClient.api.getHomeStats(userId)
                if (resp.isSuccessful) stats = resp.body()
            } catch (_: Exception) {}
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GoldSurface)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GoldBorder),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = username.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                        color = TextDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Hola, $username",
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
            Surface(
                onClick = onNavigateToRanking,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                color = GoldBorder.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Ranking",
                        tint = GoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "$points pts",
                        color = GoldDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Panel FSRS
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GoldPrimary)
            }
        } else if (stats != null) {
            FsrsStatsPanel(stats = stats!!, onStateClick = onNavigateToFsrs)
        } else {
            // Sin datos aún fallack?
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🧭", fontSize = 80.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Compass English",
                        color = GoldDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                MenuCard(
                    title = "Mis apuntes",
                    emoji = "⭐",
                    onClick = { onNavigateToMyCards() },
                    modifier = Modifier.weight(1f)
                )
                MenuCard(
                    title = "Ejercicios",
                    emoji = "✏️",
                    onClick = { onNavigateToExercises() },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                MenuCard(
                    title = "Diccionario",
                    emoji = "📖",
                    onClick = { onNavigateToDictionary() },
                    modifier = Modifier.weight(1f)
                )
                MenuCard(
                    title = "Ajustes",
                    emoji = "⚙️",
                    onClick = { onNavigateToSettings() },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

//Panel de stats FSRS :)
@Composable
fun FsrsStatsPanel(stats: HomeStats, onStateClick: (String) -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Fila 1: pendientes hoy + memoria media
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FsrsStatCard(
                emoji = "📅",
                value = "${stats.dueToday}",
                label = if (stats.dueToday == 1) "para repasar hoy" else "para repasar hoy",
                highlight = stats.dueToday > 0,
                modifier = Modifier.weight(1f)
            )
            FsrsStatCard(
                emoji = "🧠",
                value = "${stats.avgStabilityDays}d",
                label = "memoria media",
                highlight = false,
                modifier = Modifier.weight(1f)
            )
        }

        // Fila 2: estados de las tarjetas
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GoldSurfaceCard),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    "Estado de tus palabras",
                    color = TextMedium,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StateChip("🆕", "${stats.countNew}",        "Nuevas",    Color(0xFF6B9EFF), onClick = { onStateClick("NEW") })
                    StateChip("📖", "${stats.countLearning}",   "Aprendiendo", Color(0xFFFFB347), onClick = { onStateClick("LEARNING") })
                    StateChip("✅", "${stats.countReview}",     "Repaso",    Color(0xFF66BB6A), onClick = { onStateClick("REVIEW") })
                    StateChip("🔄", "${stats.countRelearning}", "Olvidadas", Color(0xFFEF5350), onClick = { onStateClick("RELEARNING") })
                }
            }
        }

        // Fila 3: recomendaciones inteligentes
        if (stats.bestTheme.isNotEmpty() || stats.worstTheme.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GoldSurfaceCard),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        "💡 Recomendaciones",
                        color = TextMedium,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(10.dp))
                    if (stats.worstTheme.isNotEmpty()) {
                        RecommendationRow(
                            emoji   = "⚠️",
                            message = "Necesitas repasar ${stats.worstTheme}",
                            detail  = "${stats.worstAccuracy}% de acierto",
                            color   = Color(0xFFEF5350)
                        )
                    }
                    if (stats.bestTheme.isNotEmpty()) {
                        if (stats.worstTheme.isNotEmpty()) {
                            Spacer(Modifier.height(6.dp))
                            HorizontalDivider(color = GoldBorder.copy(alpha = 0.4f))
                            Spacer(Modifier.height(6.dp))
                        }
                        RecommendationRow(
                            emoji   = "🎯",
                            message = "Vas bien en ${stats.bestTheme}",
                            detail  = "${stats.bestAccuracy}% de acierto",
                            color   = Color(0xFF66BB6A)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FsrsStatCard(
    emoji: String,
    value: String,
    label: String,
    highlight: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlight) GoldBorder.copy(alpha = 0.35f) else GoldSurfaceCard
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 28.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                color = if (highlight) GoldDark else TextDark,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Text(label, color = TextLight, fontSize = 11.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun StateChip(emoji: String, count: String, label: String, color: Color, onClick: () -> Unit = {}) {
    Surface(
        onClick = onClick,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
        color = androidx.compose.ui.graphics.Color.Transparent
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(6.dp)
        ) {
            Text(emoji, fontSize = 18.sp)
            Text(count, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(label, color = TextLight, fontSize = 10.sp)
        }
    }
}

@Composable
private fun RecommendationRow(emoji: String, message: String, detail: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = 18.sp)
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(message, color = TextDark, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(detail,  color = TextLight, fontSize = 11.sp)
        }
        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(detail.split(" ")[0], color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ThemeStatCard(
    emoji: String,
    theme: String,
    accuracy: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GoldSurfaceCard),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("$emoji $label", color = TextLight, fontSize = 11.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                theme,
                color = TextDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 1
            )
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { accuracy / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = GoldBorder.copy(alpha = 0.3f)
            )
            Spacer(Modifier.height(2.dp))
            Text("$accuracy%", color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}