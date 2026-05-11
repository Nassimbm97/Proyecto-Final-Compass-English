package com.compassenglish.ui.screens

import android.media.MediaPlayer
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compassenglish.domain.model.WordEntry
import com.compassenglish.presentation.DictionaryViewModelFactory
import com.compassenglish.presentation.dictionary.DictionaryViewModel
import com.compassenglish.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun DictionaryScreen(
    onBack: () -> Unit,
    vm: DictionaryViewModel = viewModel(factory = DictionaryViewModelFactory())
) {
    val state by vm.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(GoldSurface)) {

        // TopBar
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = GoldDark)
            }
            Text("📖  Diccionario", color = TextDark,
                fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.weight(1f))
            Text("${state.words.size} palabras", color = TextLight, fontSize = 12.sp)
        }

        // Buscador
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { vm.search(it) },
            placeholder = { Text("Buscar...", color = TextLight) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = GoldPrimary) },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) IconButton(onClick = { vm.search("") }) {
                    Icon(Icons.Default.Close, null, tint = TextLight)
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldPrimary, unfocusedBorderColor = GoldBorder,
                focusedContainerColor = GoldSurfaceCard, unfocusedContainerColor = GoldSurfaceCard,
                focusedTextColor = TextDark, unfocusedTextColor = TextDark),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 8.dp)
        )

        // Filtros de TIPO
        if (state.availableTypes.isNotEmpty()) {
            Text("  Tipo:", color = TextMedium, fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 16.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(state.availableTypes) { type ->
                    FilterChip(
                        label = typeLabel(type),
                        selected = state.selectedType == type,
                        onClick = { vm.setTypeFilter(type) }
                    )
                }
            }
        }

        // Filtros de TEMA
        if (state.availableThemes.isNotEmpty()) {
            Text("  Tema:", color = TextMedium, fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 16.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(state.availableThemes) { theme ->
                    FilterChip(
                        label = themeLabel(theme),
                        selected = state.selectedTheme == theme,
                        onClick = { vm.setThemeFilter(theme) }
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // Lista de palabras
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = GoldPrimary)
            }
            state.words.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 50.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Sin resultados", color = TextMedium)
                }
            }
            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.words, key = { it.id }) { word ->
                    WordCard(word = word, onClick = { vm.selectWord(word) })
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }

    // Bottom sheet
    state.selectedWord?.let { word ->
        WordDetailSheet(
            word = word,
            definition = state.wordDetail?.definition,
            audioUrl = state.wordDetail?.audioUrl,
            isLoadingDef = state.isLoadingDetail,
            onDismiss = { vm.clearSelection() }
        )
    }
}

// Los chips de filtros

@Composable
fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) GoldPrimary else GoldSurfaceCard)
            .border(1.dp, if (selected) GoldDark else GoldBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else TextMedium,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// Traducciones de labels
fun typeLabel(type: String) = when (type) {
    "Noun"        -> "Sustantivo"
    "Verb"        -> "Verbo"
    "Adjective"   -> "Adjetivo"
    "Adverb"      -> "Adverbio"
    "Determiner"  -> "Determinante"
    "Preposition" -> "Preposición"
    else          -> type
}

fun themeLabel(theme: String) = when (theme) {
    "Sports"    -> "🏅 Deportes"
    "Food"      -> "🍎 Comida"
    "Countries" -> "🌍 Países"
    "Anatomy"   -> "🫀 Anatomía"
    "Weather"   -> "☀️ Tiempo"
    "Hobbies"   -> "🎸 Hobbies"
    "Jobs"      -> "💼 Trabajos"
    "Others"    -> "📦 Otros"
    else        -> theme
}


@Composable
fun WordCard(word: WordEntry, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GoldSurfaceCard),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(word.wordSpa, color = TextDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (word.isFalseFriend) { Spacer(Modifier.width(6.dp)); Text("⚠️", fontSize = 13.sp) }
                }
                Text(word.wordEng, color = GoldDark, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (word.type.isNotEmpty()) TypeChip(typeLabel(word.type))
                    word.themes.take(2).forEach { ThemeChip(themeLabel(it)) }
                }
            }
            LevelBadge(word.level)
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Default.ChevronRight, null, tint = TextLight, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun TypeChip(label: String) {
    Box(modifier = Modifier
        .background(Color(0xFFEDE7F6), RoundedCornerShape(20.dp))
        .padding(horizontal = 8.dp, vertical = 3.dp)) {
        Text(label, color = Color(0xFF4527A0), fontSize = 11.sp)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailSheet(
    word: WordEntry,
    definition: String?,
    audioUrl: String?,
    isLoadingDef: Boolean,
    onDismiss: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = GoldSurfaceCard,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 24.dp).padding(bottom = 32.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(word.wordSpa, color = TextDark, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp)
                    Text(word.wordEng, color = GoldDark, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                }
                IconButton(
                    onClick = {
                        if (!audioUrl.isNullOrEmpty() && !isPlaying) {
                            isPlaying = true
                            scope.launch {
                                try {
                                    MediaPlayer().apply {
                                        setDataSource(audioUrl)
                                        prepare(); start()
                                        setOnCompletionListener { isPlaying = false }
                                    }
                                } catch (_: Exception) { isPlaying = false }
                            }
                        }
                    },
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(50))
                        .background(if (isPlaying) GoldPrimary else GoldBorder)
                ) { Text(if (isPlaying) "🔊" else "🔈", fontSize = 24.sp) }
            }

            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (word.type.isNotEmpty()) TypeChip(typeLabel(word.type))
                word.themes.forEach { ThemeChip(themeLabel(it)) }
                LevelBadge(word.level)
            }
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = GoldBorder.copy(alpha = 0.5f))
            Spacer(Modifier.height(16.dp))

            Text("Definición (Merriam-Webster)", color = TextMedium,
                fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))

            if (isLoadingDef) {
                CircularProgressIndicator(color = GoldPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = definition?.ifEmpty { "Definición no disponible" } ?: "Definición no disponible",
                    color = TextDark, fontSize = 15.sp, lineHeight = 22.sp
                )
            }

            if (word.isFalseFriend && word.feedbackHint.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Card(shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD))) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Text("⚠️", fontSize = 16.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(word.feedbackHint, color = Color(0xFF856404), fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeChip(theme: String) {
    Box(modifier = Modifier
        .background(GoldBorder.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
        .padding(horizontal = 8.dp, vertical = 3.dp)) {
        Text(theme, color = TextMedium, fontSize = 11.sp)
    }
}

@Composable
fun LevelBadge(level: String) {
    val (bg, textColor) = when (level) {
        "INTERMEDIATE" -> Color(0xFFDCEFFB) to Color(0xFF0C63E4)
        "ADVANCED"     -> Color(0xFFFFE5D0) to Color(0xFFD9480F)
        else           -> Color(0xFFD3F9D8) to Color(0xFF2B8A3E)
    }
    Box(modifier = Modifier.background(bg, RoundedCornerShape(20.dp))
        .padding(horizontal = 8.dp, vertical = 3.dp)) {
        Text(
            when (level) { "INTERMEDIATE" -> "Medio"; "ADVANCED" -> "Avanzado"; else -> "Básico" },
            color = textColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold
        )
    }
}
