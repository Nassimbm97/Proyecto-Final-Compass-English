package com.compassenglish.presentation.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compassenglish.domain.model.*
import com.compassenglish.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.compassenglish.data.api.RetrofitClient
import com.compassenglish.data.repository.toDomain
import com.compassenglish.ui.screens.SessionConfig

data class ExerciseUiState(
    val isLoading: Boolean = true,
    val exercises: List<Exercise> = emptyList(),
    val currentIndex: Int = 0,
    val totalPoints: Int = 0,
    val sessionDone: Boolean = false,
    val error: String = "",
    // Resultado de la última respuesta
    val lastResult: AnswerResult? = null,
    val answered: Boolean = false
)

class ExerciseViewModel(
    private val getSessionUseCase: GetSessionUseCase,
    private val submitAnswerUseCase: SubmitAnswerUseCase,
    private val finishSessionUseCase: FinishSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    fun loadSession(userId: Int, config: SessionConfig = SessionConfig()) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val resp = RetrofitClient.api.getSessionFiltered(
                    userId = userId,
                    theme  = config.theme,
                    type   = config.exerciseType,
                    source = config.source,
                    level  = config.level
                )
                if (resp.isSuccessful) {
                    val exercises = resp.body()!!.map { exercise -> exercise.toDomain() }
                    // Limitar a 20 estrictamente
                    val limited = exercises.take(20)
                    _uiState.update { it.copy(isLoading = false, exercises = limited) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Error al cargar sesión") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error") }
            }
        }
    }

    fun submitAnswer(userId: Int, answer: String, responseMs: Int) {
        val state = _uiState.value
        val current = state.exercises.getOrNull(state.currentIndex) ?: return

        viewModelScope.launch {
            val result = submitAnswerUseCase(
                userId       = userId,
                wordId       = current.wordId,
                answer       = answer,
                exerciseType = current.type.name,
                responseMs   = responseMs,
                isCustom     = current.isCustom
            )
            when (result) {
                is Result.Success -> _uiState.update {
                    it.copy(
                        lastResult  = result.data,
                        answered    = true,
                        totalPoints = if (result.data.correct) it.totalPoints + 1 else it.totalPoints
                    )
                }
                is Result.Error -> _uiState.update {
                    it.copy(lastResult = AnswerResult(false, result.message, "", 0), answered = true)
                }
                else -> Unit
            }
        }
    }

    fun nextExercise() {
        val state = _uiState.value
        val next = state.currentIndex + 1
        if (next >= state.exercises.size) {
            _uiState.update { it.copy(sessionDone = true, answered = false, lastResult = null) }
        } else {
            _uiState.update { it.copy(currentIndex = next, answered = false, lastResult = null) }
        }
    }

    fun finishSession(userId: Int, onDone: () -> Unit) {
        viewModelScope.launch {
            finishSessionUseCase(userId)
            onDone()
        }
    }
}