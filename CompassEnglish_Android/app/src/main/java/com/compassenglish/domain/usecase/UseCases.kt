package com.compassenglish.domain.usecase

import com.compassenglish.domain.model.*
import com.compassenglish.domain.repository.*

// Casos de uso
//  Cada caso de uso encapsula UNA operación de negocio.




class LoginUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(username: String, password: String): Result<User> {
        if (username.isBlank()) return Result.Error("El usuario no puede estar vacío")
        if (password.isBlank()) return Result.Error("La contraseña no puede estar vacía")
        return repo.login(username.trim(), password)
    }
}

class RegisterUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(
        username: String,
        password: String,
        confirmPassword: String,
        email: String,
        learningMode: String = "FREE"
    ): Result<User> {
        if (username.isBlank() || password.isBlank() || email.isBlank())
            return Result.Error("Rellena todos los campos")
        if (password != confirmPassword)
            return Result.Error("Las contraseñas no coinciden")
        if (!email.contains("@"))
            return Result.Error("Email no válido")
        return repo.register(username.trim(), password, email.trim(), learningMode)
    }
}

// Ejercicios

class GetSessionUseCase(private val repo: ExerciseRepository) {
    suspend operator fun invoke(userId: Int): Result<List<Exercise>> =
        repo.getSession(userId)
}

class SubmitAnswerUseCase(private val repo: ExerciseRepository) {
    suspend operator fun invoke(
        userId: Int,
        wordId: Int,
        answer: String,
        exerciseType: String,
        responseMs: Int,
        isCustom: Boolean = false
    ): Result<AnswerResult> {
        if (answer.isBlank()) return Result.Error("Escribe una respuesta")
        return repo.submitAnswer(userId, wordId, answer.trim(), exerciseType, responseMs, isCustom)
    }
}

class FinishSessionUseCase(private val repo: ExerciseRepository) {
    suspend operator fun invoke(userId: Int): Result<Unit> =
        repo.finishSession(userId)
}

//Diccionario

class GetAllWordsUseCase(private val repo: DictionaryRepository) {
    suspend operator fun invoke(): Result<List<WordEntry>> = repo.getAllWords()
}

class SearchWordsUseCase(private val repo: DictionaryRepository) {
    suspend operator fun invoke(query: String): Result<List<WordEntry>> =
        if (query.isBlank()) repo.getAllWords()
        else repo.searchWords(query)
}

class GetWordDetailUseCase(private val repo: DictionaryRepository) {
    suspend operator fun invoke(wordEng: String): Result<WordDetail> =
        repo.getDefinition(wordEng)
}

//  Progreso

class GetProgressUseCase(private val repo: ProgressRepository) {
    suspend operator fun invoke(userId: Int): Result<List<ProgressEntry>> =
        repo.getProgress(userId)
}

// -Ranking

class GetRankingUseCase(private val repo: RankingRepository) {
    suspend operator fun invoke(limit: Int = 20): Result<List<RankingEntry>> =
        repo.getRanking(limit)
}