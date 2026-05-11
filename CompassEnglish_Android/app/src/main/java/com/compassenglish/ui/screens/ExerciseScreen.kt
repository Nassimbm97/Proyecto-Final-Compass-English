package com.compassenglish.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compassenglish.domain.model.ExerciseType
import com.compassenglish.presentation.ExerciseViewModelFactory
import com.compassenglish.presentation.exercise.ExerciseViewModel
import com.compassenglish.ui.components.*
import com.compassenglish.ui.theme.*

@Composable
fun ExerciseScreen(
    userId: Int,
    sessionConfig: SessionConfig = SessionConfig(),
    onFinish: () -> Unit,
    vm: ExerciseViewModel = viewModel(factory = ExerciseViewModelFactory())
) {
    val state by vm.uiState.collectAsState()

    LaunchedEffect(userId) { vm.loadSession(userId, sessionConfig) }
    Box(modifier = Modifier.fillMaxSize().background(GoldSurface)) {
        when {
            state.isLoading -> CircularProgressIndicator(
                color = GoldPrimary,
                modifier = Modifier.align(Alignment.Center))

            state.error.isNotEmpty() -> EmptyState(
                emoji = "😕", message = state.error, onBack = onFinish)

            state.sessionDone -> SessionResultScreen(
                totalExercises = state.exercises.size,
                points = state.totalPoints,
                onFinish = { vm.finishSession(userId) { onFinish() } }
            )

            state.exercises.isEmpty() -> EmptyState(
                emoji = "📚",
                message = "No hay palabras disponibles.\nAñade vocabulario desde el diccionario.",
                onBack = onFinish
            )

            else -> {
                Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {

                    // Barra de progreso
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onFinish) { Text("❌", fontSize = 28.sp) }
                        Spacer(Modifier.width(8.dp))
                        LinearProgressIndicator(
                            progress = { (state.currentIndex + 1f) / state.exercises.size },
                            modifier = Modifier.weight(1f).height(8.dp),
                            color = GoldPrimary, trackColor = GoldBorder
                        )
                        Spacer(Modifier.width(12.dp))
                        Text("${state.currentIndex + 1}/${state.exercises.size}",
                            color = TextMedium, fontSize = 13.sp)
                    }

                    Spacer(Modifier.height(24.dp))

                    AnimatedContent(
                        targetState = state.currentIndex,
                        transitionSpec = {
                            slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                        },
                        label = "exercise"
                    ) { idx ->
                        val ex = state.exercises[idx]
                        var userAnswer by remember { mutableStateOf("") }
                        var startTime  by remember { mutableLongStateOf(System.currentTimeMillis()) }

                        LaunchedEffect(idx) {
                            userAnswer = ""
                            startTime  = System.currentTimeMillis()
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            when (ex.type) {

                                // multiple choice

                                ExerciseType.MULTIPLE_CHOICE -> {
                                    Text("¿Cómo se dice en inglés?",
                                        color = TextMedium, fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold)
                                    Spacer(Modifier.height(20.dp))
                                    ExerciseCard {
                                        Text(ex.wordSpa, color = TextDark,
                                            fontSize = 28.sp, fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth())
                                    }
                                    Spacer(Modifier.height(20.dp))
                                    ex.options.forEach { option ->
                                        val optState = when {
                                            !state.answered            -> OptionState.DEFAULT
                                            option == ex.wordEng       -> OptionState.CORRECT
                                            option == userAnswer
                                                    && state.lastResult?.correct == false -> OptionState.WRONG
                                            else                       -> OptionState.DISABLED
                                        }
                                        OptionButton(text = option, state = optState, onClick = {
                                            if (!state.answered) {
                                                userAnswer = option
                                                vm.submitAnswer(userId, option,
                                                    (System.currentTimeMillis() - startTime).toInt())
                                            } else vm.nextExercise()
                                        })
                                        Spacer(Modifier.height(10.dp))
                                    }
                                    // Feedback multiple choice
                                    state.lastResult?.let { result ->
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            if (result.correct) "✅ ¡Correcto!"
                                            else "❌ Era \"${result.correctAnswer ?: ""}\"",
                                            color = if (result.correct)
                                                Color(0xFF2E7D32) else Color(0xFFB71C1C),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }


                                // FILL THE GAP
                                ExerciseType.FILL_GAP -> {
                                    Text("Completa la frase",
                                        color = TextMedium, fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold)
                                    Spacer(Modifier.height(20.dp))

                                    // Frase con el huequito
                                    ExerciseCard {
                                        Text(
                                            text = ex.wordSpa.replace("___", "______"),
                                            color = TextDark, fontSize = 18.sp,
                                            lineHeight = 26.sp, textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }

                                    Spacer(Modifier.height(20.dp))


                                    ex.options.forEach { option ->
                                        val optState = when {
                                            !state.answered            -> OptionState.DEFAULT
                                            option == ex.wordEng       -> OptionState.CORRECT
                                            option == userAnswer
                                                    && state.lastResult?.correct == false -> OptionState.WRONG
                                            else                       -> OptionState.DISABLED
                                        }
                                        OptionButton(text = option, state = optState, onClick = {
                                            if (!state.answered) {
                                                userAnswer = option
                                                vm.submitAnswer(userId, option,
                                                    (System.currentTimeMillis() - startTime).toInt())
                                            } else vm.nextExercise()
                                        })
                                        Spacer(Modifier.height(10.dp))
                                    }

                                    // Feedback fill-the-gap
                                    state.lastResult?.let { result ->
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            if (result.correct) "✅ ¡Correcto!"
                                            else "❌ Era \"${result.correctAnswer ?: ""}\"",
                                            color = if (result.correct)
                                                Color(0xFF2E7D32) else Color(0xFFB71C1C),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.height(12.dp))
                                        GoldButton(
                                            text = "Siguiente ➡️",
                                            onClick = { vm.nextExercise() }
                                        )
                                    }
                                }


                                // TRANSLATE
                                else -> {
                                    Text("Escribe la palabra en inglés",
                                        color = TextMedium, fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold)
                                    Spacer(Modifier.height(20.dp))
                                    ExerciseCard {
                                        Text(ex.wordSpa, color = TextDark,
                                            fontSize = 22.sp, fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth())
                                        ex.hint?.let {
                                            Spacer(Modifier.height(6.dp))
                                            Text("💡 $it", color = TextLight, fontSize = 12.sp,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center)
                                        }
                                    }
                                    Spacer(Modifier.height(20.dp))
                                    OutlinedTextField(
                                        value = userAnswer,
                                        onValueChange = { if (!state.answered) userAnswer = it },
                                        placeholder = { Text("Tu respuesta...", color = TextLight) },
                                        enabled = !state.answered,
                                        shape = RoundedCornerShape(14.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor      = GoldPrimary,
                                            unfocusedBorderColor    = GoldBorder,
                                            focusedContainerColor   = GoldSurfaceCard,
                                            unfocusedContainerColor = GoldSurfaceCard,
                                            focusedTextColor        = TextDark,
                                            unfocusedTextColor      = TextDark),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    state.lastResult?.let { result ->
                                        Card(
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (result.correct)
                                                    Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = if (result.correct) "✅ ¡Correcto!"
                                                else "❌ ${result.feedback.orEmpty().ifEmpty {
                                                    "La respuesta es \"${result.correctAnswer ?: ""}\""
                                                }}",
                                                color = if (result.correct)
                                                    Color(0xFF2E7D32) else Color(0xFFB71C1C),
                                                fontSize = 14.sp,
                                                modifier = Modifier.padding(12.dp))
                                        }
                                        Spacer(Modifier.height(16.dp))
                                    }
                                    GoldButton(
                                        text = if (state.answered) "Siguiente ➡️" else "Comprobar",
                                        enabled = userAnswer.isNotBlank() || state.answered,
                                        onClick = {
                                            if (state.answered) vm.nextExercise()
                                            else vm.submitAnswer(userId, userAnswer,
                                                (System.currentTimeMillis() - startTime).toInt())
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(emoji: String, message: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 60.sp)
        Spacer(Modifier.height(16.dp))
        Text(message, color = TextMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        GoldButton("Volver", onClick = onBack)
    }
}

@Composable
fun SessionResultScreen(totalExercises: Int, points: Int, onFinish: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(GoldSurface).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(if (points >= totalExercises * 0.7) "🎉" else "💪", fontSize = 90.sp)
        Spacer(Modifier.height(24.dp))
        Text("Sesión completada", color = TextDark,
            fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text("$points de $totalExercises correctos", color = TextMedium, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))
        val pct = if (totalExercises > 0) (points * 100) / totalExercises else 0
        Text("Precisión: $pct%", color = GoldDark,
            fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(40.dp))
        GoldButton("Volver al inicio", onClick = onFinish)
    }
}