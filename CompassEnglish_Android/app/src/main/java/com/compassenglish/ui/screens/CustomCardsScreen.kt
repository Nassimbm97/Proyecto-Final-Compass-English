package com.compassenglish.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compassenglish.data.api.CreateCustomCardRequest
import com.compassenglish.data.api.UpdateCustomCardRequest
import com.compassenglish.data.api.CustomCardResponse
import com.compassenglish.data.api.RetrofitClient
import com.compassenglish.ui.components.GoldButton
import com.compassenglish.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CustomCardData(
    val id: Int = 0,
    val wordSpa: String = "",
    val wordEng: String = "",
    val notes: String = "",
    val level: String = "BEGINNER",
    val theme: String = ""
)

private val LEVELS = listOf("BEGINNER", "INTERMEDIATE", "ADVANCED")
private val LEVEL_LABELS = mapOf(
    "BEGINNER"     to "Principiante",
    "INTERMEDIATE" to "Intermedio",
    "ADVANCED"     to "Avanzado"
)

@Composable
fun CustomCardsScreen(userId: Int, onBack: () -> Unit) {
    var cards           by remember { mutableStateOf<List<CustomCardData>>(emptyList()) }
    var isLoading       by remember { mutableStateOf(true) }
    var showAddSheet    by remember { mutableStateOf(false) }
    var cardToEdit      by remember { mutableStateOf<CustomCardData?>(null) }
    val scope = rememberCoroutineScope()

    fun loadCards() {
        scope.launch {
            isLoading = true
            try {
                val resp = RetrofitClient.api.getCustomCards(userId)
                if (resp.isSuccessful) {
                    cards = resp.body()?.map {
                        CustomCardData(
                            id      = it.id,
                            wordSpa = it.wordSpa,
                            wordEng = it.wordEng,
                            notes   = it.notes,
                            level   = it.level,
                            theme   = it.theme
                        )
                    } ?: emptyList()
                }
            } catch (_: Exception) {}
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadCards() }

    Column(modifier = Modifier.fillMaxSize().background(GoldSurface)) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = GoldDark)
            }
            Text("⭐  Mis apuntes", color = TextDark,
                fontWeight = FontWeight.Bold, fontSize = 20.sp,
                modifier = Modifier.weight(1f))
            IconButton(onClick = { showAddSheet = true }) {
                Icon(Icons.Default.Add, null, tint = GoldPrimary)
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = GoldPrimary)
            }
        } else if (cards.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📭", fontSize = 60.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("No tienes apuntes todavía",
                        color = TextMedium, fontSize = 15.sp)
                    Spacer(Modifier.height(16.dp))
                    GoldButton("Crear primer apunte",
                        onClick = { showAddSheet = true },
                        modifier = Modifier.padding(horizontal = 40.dp))
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cards, key = { it.id }) { card ->
                    CustomCardItem(
                        card = card,
                        onEdit = { cardToEdit = card },
                        onDelete = {
                            scope.launch {
                                try {
                                    RetrofitClient.api.deleteCustomCard(card.id)
                                    loadCards()
                                } catch (_: Exception) {}
                            }
                        }
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }

    if (showAddSheet) {
        AddEditCustomCardSheet(
            userId    = userId,
            cardToEdit = null,
            onDismiss = { showAddSheet = false },
            onSaved   = { showAddSheet = false; loadCards() }
        )
    }

    cardToEdit?.let { editing ->
        AddEditCustomCardSheet(
            userId     = userId,
            cardToEdit = editing,
            onDismiss  = { cardToEdit = null },
            onSaved    = { cardToEdit = null; loadCards() }
        )
    }
}

@Composable
fun CustomCardItem(card: CustomCardData, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showConfirm by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GoldSurfaceCard),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(card.wordSpa, color = TextDark,
                    fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(card.wordEng, color = GoldDark,
                    fontSize = 14.sp, fontWeight = FontWeight.Medium)
                if (card.notes.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text("📝 ${card.notes}", color = TextLight, fontSize = 12.sp)
                }
                if (card.theme.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text("🏷 ${card.theme}", color = TextLight, fontSize = 11.sp)
                }
            }
            LevelBadge(card.level)
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, null,
                    tint = GoldPrimary, modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = { showConfirm = true }) {
                Icon(Icons.Default.Delete, null,
                    tint = Color(0xFFE53935), modifier = Modifier.size(20.dp))
            }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Eliminar apunte") },
            text = { Text("¿Eliminar \"${card.wordSpa}\"?") },
            confirmButton = {
                TextButton(onClick = { showConfirm = false; onDelete() }) {
                    Text("Eliminar", color = Color(0xFFE53935))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Cancelar") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCustomCardSheet(
    userId: Int,
    cardToEdit: CustomCardData?,
    onDismiss: () -> Unit,
    onSaved: () -> Unit
) {
    val isEditing = cardToEdit != null

    var wordSpa      by remember { mutableStateOf(cardToEdit?.wordSpa ?: "") }
    var wordEng      by remember { mutableStateOf(cardToEdit?.wordEng ?: "") }
    var notes        by remember { mutableStateOf(cardToEdit?.notes ?: "") }
    var selectedLevel by remember { mutableStateOf(cardToEdit?.level ?: "BEGINNER") }
    var theme        by remember { mutableStateOf(cardToEdit?.theme ?: "") }
    var suggestions  by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoadingSug by remember { mutableStateOf(false) }
    var isSaving     by remember { mutableStateOf(false) }
    var error        by remember { mutableStateOf("") }
    var levelExpanded by remember { mutableStateOf(false) }

    // Temas del backend
    var availableThemes by remember { mutableStateOf<List<String>>(emptyList()) }
    var themeExpanded   by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            val resp = RetrofitClient.api.getThemes()
            if (resp.isSuccessful) availableThemes = resp.body() ?: emptyList()
        } catch (_: Exception) {}
    }

    // Sugerencias Merriam-Webster con debounce para no cancelar peticiones
    LaunchedEffect(wordSpa) {
        if (wordSpa.length >= 3) {
            delay(400)
            isLoadingSug = true
            try {
                val resp = RetrofitClient.api.getMerriamSuggestions(wordSpa)
                if (resp.isSuccessful) {
                    suggestions = resp.body()?.suggestions ?: emptyList()
                }
            } catch (_: Exception) {}
            isLoadingSug = false
        } else {
            suggestions = emptyList()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = GoldSurfaceCard,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                if (isEditing) "Editar apunte" else "Nuevo apunte",
                color = TextDark, fontWeight = FontWeight.Bold, fontSize = 20.sp
            )
            Spacer(Modifier.height(20.dp))

            //  Palabra en español
            OutlinedTextField(
                value = wordSpa,
                onValueChange = { wordSpa = it },
                label = { Text("Palabra en español") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = GoldPrimary,
                    unfocusedBorderColor = GoldBorder)
            )

            if (isLoadingSug) {
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = GoldPrimary, strokeWidth = 2.dp)
                    Spacer(Modifier.width(6.dp))
                    Text("Buscando en Merriam-Webster...",
                        color = TextLight, fontSize = 12.sp)
                }
            } else if (suggestions.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text("Sugerencias de traducción:", color = TextLight, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    suggestions.take(4).forEach { sug ->
                        SuggestionChip(
                            onClick = { wordEng = sug },
                            label = { Text(sug, fontSize = 12.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = GoldBorder.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Traducción al inglés
            OutlinedTextField(
                value = wordEng,
                onValueChange = { wordEng = it },
                label = { Text("Traducción al inglés") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = GoldPrimary,
                    unfocusedBorderColor = GoldBorder)
            )

            Spacer(Modifier.height(12.dp))

            // Apuntes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Apuntes (opcional)") },
                minLines = 2, maxLines = 4,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = GoldPrimary,
                    unfocusedBorderColor = GoldBorder)
            )

            Spacer(Modifier.height(12.dp))

            // --- Selector de nivel ---
            ExposedDropdownMenuBox(
                expanded = levelExpanded,
                onExpandedChange = { levelExpanded = it }
            ) {
                OutlinedTextField(
                    value = LEVEL_LABELS[selectedLevel] ?: selectedLevel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nivel") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(levelExpanded) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = GoldPrimary,
                        unfocusedBorderColor = GoldBorder)
                )
                ExposedDropdownMenu(
                    expanded = levelExpanded,
                    onDismissRequest = { levelExpanded = false }
                ) {
                    LEVELS.forEach { lvl ->
                        DropdownMenuItem(
                            text = { Text(LEVEL_LABELS[lvl] ?: lvl) },
                            onClick = { selectedLevel = lvl; levelExpanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // SelectoR de tema
            if (availableThemes.isNotEmpty()) {
                ExposedDropdownMenuBox(
                    expanded = themeExpanded,
                    onExpandedChange = { themeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = theme.ifEmpty { "Sin tema" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tema") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(themeExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = GoldPrimary,
                            unfocusedBorderColor = GoldBorder)
                    )
                    ExposedDropdownMenu(
                        expanded = themeExpanded,
                        onDismissRequest = { themeExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sin tema") },
                            onClick = { theme = ""; themeExpanded = false }
                        )
                        availableThemes.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = { theme = t; themeExpanded = false }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            if (error.isNotEmpty()) {
                Text(error, color = Color(0xFFE53935), fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(8.dp))

            GoldButton(
                text = when {
                    isSaving  -> if (isEditing) "Guardando..." else "Guardando..."
                    isEditing -> "Guardar cambios"
                    else      -> "Guardar apunte"
                },
                enabled = !isSaving,
                onClick = {
                    if (wordSpa.isBlank() || wordEng.isBlank()) {
                        error = "Rellena palabra y traducción"
                        return@GoldButton
                    }
                    isSaving = true
                    scope.launch {
                        try {
                            if (isEditing) {
                                RetrofitClient.api.updateCustomCard(
                                    cardToEdit!!.id,
                                    UpdateCustomCardRequest(
                                        wordSpa = wordSpa.trim(),
                                        wordEng = wordEng.trim(),
                                        notes   = notes.trim(),
                                        level   = selectedLevel,
                                        theme   = theme.trim()
                                    )
                                )
                            } else {
                                RetrofitClient.api.createCustomCard(
                                    CreateCustomCardRequest(
                                        userId  = userId,
                                        wordSpa = wordSpa.trim(),
                                        wordEng = wordEng.trim(),
                                        notes   = notes.trim(),
                                        level   = selectedLevel,
                                        theme   = theme.trim()
                                    )
                                )
                            }
                            onSaved()
                        } catch (e: Exception) {
                            error = "Error al guardar: ${e.message}"
                            isSaving = false
                        }
                    }
                }
            )
        }
    }
}
