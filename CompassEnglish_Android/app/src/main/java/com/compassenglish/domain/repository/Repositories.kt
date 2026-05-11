package com.compassenglish.domain.repository

import com.compassenglish.domain.model.*

//  interfaces de repositorio
//  El dominio define el contrato. La capa data lo implementa.


interface AuthRepository {
    suspend fun login(username: String, password: String): Result<User>
    suspend fun register(username: String, password: String, email: String, birthDate: String): Result<User>
}

interface ExerciseRepository {
    suspend fun getSession(userId: Int): Result<List<Exercise>>
    suspend fun submitAnswer(
        userId: Int,
        wordId: Int,
        answer: String,
        exerciseType: String,
        responseMs: Int,
        isCustom: Boolean = false
    ): Result<AnswerResult>
    suspend fun finishSession(userId: Int): Result<Unit>
}

interface DictionaryRepository {
    suspend fun getAllWords(): Result<List<WordEntry>>
    suspend fun searchWords(query: String): Result<List<WordEntry>>
    suspend fun getDefinition(wordEng: String): Result<WordDetail>
}

interface ProgressRepository {
    suspend fun getProgress(userId: Int): Result<List<ProgressEntry>>
}

interface RankingRepository {
    suspend fun getRanking(limit: Int): Result<List<RankingEntry>>
}