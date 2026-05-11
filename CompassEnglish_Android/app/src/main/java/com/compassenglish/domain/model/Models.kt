package com.compassenglish.domain.model


// Modelos puros sin dependencias externas


data class User(
    val id: Int,
    val username: String,
    val email: String,
    val points: Int,
    val premium: Boolean,
    val learningMode: String,
    val levelEstimated: String,
    val role: String
)

data class Exercise(
    val wordId: Int,
    val wordSpa: String,
    val wordEng: String,
    val level: String,
    val type: ExerciseType,
    val options: List<String>,
    val hint: String?,
    val isCustom: Boolean = false
)

enum class ExerciseType { TRANSLATE, MULTIPLE_CHOICE, FILL_GAP }

data class AnswerResult(
    val correct: Boolean,
    val feedback: String?,
    val correctAnswer: String?,
    val points: Int
)

data class WordEntry(
    val id: Int,
    val wordSpa: String,
    val wordEng: String,
    val level: String,
    val type: String,
    val themes: List<String>,
    val isFalseFriend: Boolean,
    val feedbackHint: String
)

data class WordDetail(
    val word: String,
    val definition: String,
    val audioUrl: String
)

data class ProgressEntry(
    val theme: String,
    val totalAttempts: Int,
    val correctAttempts: Int,
    val accuracyPct: Int,
    val trend: String
)

data class RankingEntry(
    val id: Int,
    val username: String,
    val points: Int,
    val premium: Boolean,
    val levelEstimated: String
)

// Resultado genérico para manejar éxito/error sin excepciones
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}