package com.compassenglish.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.compassenglish.data.api.FsrsWord
import com.compassenglish.data.api.RetrofitClient
import com.compassenglish.ui.theme.*
import kotlinx.coroutines.launch

private data class StateInfo(
    val key:         String,
    val emoji:       String,
    val title:       String,
    val description: String,
    val color:       Color
)

private val STATE_INFO = listOf(
    StateInfo("NEW",        "🆕", "Nuevas",
        "Palabras que el algoritmo ha seleccionado para ti pero que aún no has practicado ninguna vez.",
        Color(0xFF6B9EFF)),
    StateInfo("LEARNING",   "📖", "Aprendiendo",
        "Las has visto al menos una vez. FSRS las repasará en días cortos hasta que las consolides.",
        Color(0xFFFFB347)),
    StateInfo("REVIEW",     "✅", "En repaso",
        "Palabras consolidadas. FSRS las espaciará semanas o meses según tu curva de memoria real.",
        Color(0xFF66BB6A)),
    StateInfo("RELEARNING", "🔄", "Olvidadas",
        "Fallaste en una palabra consolidada. Volverán pronto para reforzar la memoria.",
        Color(0xFFEF5350))
)

@Composable
fun FsrsDetailScreen(
    userId: Int,
    initialState: String,
    onBack: () -> Unit
) {
    var selectedState by remember { mutableStateOf(initialState) }
    var words         by remember { mutableStateOf<List<FsrsWord>>(emptyList()) }
    var isLoading     by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    fun loadWords(state: String) {
        isLoading = true
        scope.launch {
            try {
                val resp = RetrofitClient.api.getFsrsWords(userId, state)
                if (resp.isSuccessful) words = resp.body() ?: emptyList()
            } catch (_: Exception) {}
            isLoading = false
        }
    }

    LaunchedEffect(selectedState) { loadWords(selectedState) }

    val info = STATE_INFO.first { it.key == selectedState }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GoldSurface)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = GoldDark)
            }
            Text(
                "Mi progreso FSRS",
                color = TextDark,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        // Selector de estado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            STATE_INFO.forEach { s ->
                val sel = s.key == selectedState
                Surface(
                    onClick = { selectedState = s.key },
                    shape = RoundedCornerShape(20.dp),
                    color = if (sel) s.color.copy(alpha = 0.2f) else GoldSurfaceCard,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(s.emoji, fontSize = 18.sp)
                        if (sel) Text(
                            s.title,
                            color = s.color,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Descripción del estado
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = info.color.copy(alpha = 0.1f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(info.emoji, fontSize = 28.sp)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        info.title,
                        color = info.color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        info.description,
                        color = TextMedium,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Lista de palabras
        if (isLoading) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = GoldPrimary)
            }
        } else if (words.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎉", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "No hay palabras en este estado",
                        color = TextMedium,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            Text(
                "${words.size} palabras",
                color = TextLight,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(6.dp))
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(words) { word ->
                    FsrsWordRow(word = word, stateColor = info.color)
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun FsrsWordRow(word: FsrsWord, stateColor: Color) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GoldSurfaceCard),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    word.wordSpa,
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Text(
                    word.wordEng,
                    color = GoldDark,
                    fontSize = 13.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                if (word.stability > 0) {
                    Text(
                        "🧠 ${word.stability}d",
                        color = stateColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (word.lapses > 0) {
                    Text(
                        "⚠️ ${word.lapses} fallos",
                        color = Color(0xFFEF5350),
                        fontSize = 11.sp
                    )
                }
                if (word.dueDate.isNotEmpty()) {
                    Text(
                        "📅 ${word.dueDate}",
                        color = TextLight,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
