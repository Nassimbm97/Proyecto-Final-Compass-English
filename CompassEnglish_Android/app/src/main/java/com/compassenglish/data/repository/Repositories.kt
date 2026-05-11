package com.compassenglish.data.repository

import com.compassenglish.data.api.*
import com.compassenglish.domain.model.*
import com.compassenglish.domain.repository.*


//  Mapean DTOs de Retrofit a modelos de dominio
//  El dominio no sabe que existe Retrofit??


class AuthRepositoryImpl(private val api: ApiService) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<User> =
        safeCall {
            val resp = api.login(LoginRequest(username, password))
            if (resp.isSuccessful) resp.body()!!.toDomain()
            else throw Exception(resp.errorBody()?.string() ?: "Error de login")
        }

    override suspend fun register(
        username: String, password: String,
        email: String, learningMode: String
    ): Result<User> = safeCall {
        val resp = api.register(RegisterRequest(username, password, email, learningMode))
        if (resp.isSuccessful) {
            resp.body()!!.toDomain()
        } else {
            // Leer el mensaje exacto que devuelve el backend (ej: "Username ya en uso")
            val errorMsg = try {
                val json = resp.errorBody()?.string() ?: ""
                org.json.JSONObject(json).optString("error", "Error al registrar")
            } catch (_: Exception) { "Error al registrar" }
            throw Exception(errorMsg)
        }
    }
}

class ExerciseRepositoryImpl(private val api: ApiService) : ExerciseRepository {

    override suspend fun getSession(userId: Int): Result<List<Exercise>> =
        safeCall {
            val resp = api.getSession(userId)
            if (resp.isSuccessful) resp.body()!!.map { it.toDomain() }
            else throw Exception("Error al cargar la sesión")
        }

    override suspend fun submitAnswer(
        userId: Int, wordId: Int, answer: String,
        exerciseType: String, responseMs: Int,
        isCustom: Boolean
    ): Result<AnswerResult> = safeCall {
        val resp = api.submitAnswer(
            AnswerRequest(userId, wordId, answer, exerciseType, responseMs, isCustom)
        )
        if (resp.isSuccessful) resp.body()!!.toDomain()
        else throw Exception("Error al enviar respuesta")
    }

    override suspend fun finishSession(userId: Int): Result<Unit> = safeCall {
        api.finishSession(userId)
    }
}

class DictionaryRepositoryImpl(private val api: ApiService) : DictionaryRepository {

    override suspend fun getAllWords(): Result<List<WordEntry>> = safeCall {
        val resp = api.getAllWords()
        if (resp.isSuccessful) resp.body()!!.map { it.toDomain() }
        else throw Exception("Error al cargar el diccionario")
    }

    override suspend fun searchWords(query: String): Result<List<WordEntry>> = safeCall {
        val resp = api.searchWords(query)
        if (resp.isSuccessful) resp.body()!!.map { it.toDomain() }
        else throw Exception("Error en la búsqueda")
    }

    override suspend fun getDefinition(wordEng: String): Result<WordDetail> = safeCall {
        val resp = api.getDefinition(wordEng)
        if (resp.isSuccessful) resp.body()!!.toDomain()
        else throw Exception("Definición no disponible")
    }
}

class ProgressRepositoryImpl(private val api: ApiService) : ProgressRepository {
    override suspend fun getProgress(userId: Int): Result<List<ProgressEntry>> = safeCall {
        val resp = api.getProgress(userId)
        if (resp.isSuccessful) resp.body()!!.map { it.toDomain() }
        else throw Exception("Error al cargar progreso")
    }
}

class RankingRepositoryImpl(private val api: ApiService) : RankingRepository {
    override suspend fun getRanking(limit: Int): Result<List<RankingEntry>> = safeCall {
        val resp = api.getRanking(limit)
        if (resp.isSuccessful) resp.body()!!.map { it.toDomain() }
        else throw Exception("Error al cargar ranking")
    }
}


fun UserResponse.toDomain() = User(
    id = id, username = username, email = email,
    points = points, premium = premium,
    learningMode = learningMode,
    levelEstimated = levelEstimated.ifBlank { "BEGINNER" },
    role = role
)

fun WordExercise.toDomain() = Exercise(
    wordId = wordId, wordSpa = wordSpa, wordEng = wordEng,
    level = level,
    type = when (type) {
        "MULTIPLE_CHOICE" -> com.compassenglish.domain.model.ExerciseType.MULTIPLE_CHOICE
        "FILL_GAP"        -> com.compassenglish.domain.model.ExerciseType.FILL_GAP
        else              -> com.compassenglish.domain.model.ExerciseType.TRANSLATE
    },
    options = options,
    hint = hint,
    isCustom = isCustom
)

fun AnswerResponse.toDomain() = AnswerResult(
    correct = correct, feedback = feedback ?: "",
    correctAnswer = correctAnswer ?: "", points = points
)

fun DictionaryWord.toDomain() = WordEntry(
    id = id, wordSpa = wordSpa, wordEng = wordEng,
    level = level, type = type, themes = themes,
    isFalseFriend = isFalseFriend, feedbackHint = feedbackHint
)

fun WordDefinition.toDomain() = WordDetail(
    word = word, definition = definition, audioUrl = audioUrl
)

fun ProgressItem.toDomain() = ProgressEntry(
    theme = theme, totalAttempts = totalAttempts,
    correctAttempts = correctAttempts, accuracyPct = accuracyPct, trend = trend
)

fun RankingItem.toDomain() = RankingEntry(
    id = id, username = username, points = points,
    premium = premium, levelEstimated = levelEstimated
)

// ================================================================
//  HELPER — captura excepciones de red en Result
// ================================================================

suspend fun <T> safeCall(block: suspend () -> T): Result<T> =
    try { Result.Success(block()) }
    catch (e: Exception) { Result.Error(e.message ?: "Error desconocido") }