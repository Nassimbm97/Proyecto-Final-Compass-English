package com.compassenglish.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

data class UserResponse(
    val id: Int = 0, val username: String = "", val email: String = "",
    val points: Int = 0, val premium: Boolean = false,
    val learningMode: String = "FREE", val levelEstimated: String = "BEGINNER",
    val role: String = "USER", val error: String? = null
)
data class LoginRequest(val username: String, val password: String)
data class RegisterRequest(val username: String, val password: String, val email: String, val learningMode: String = "FREE")
data class WordExercise(
    val wordId: Int = 0, val wordSpa: String = "", val wordEng: String = "",
    val level: String = "BEGINNER", val type: String = "TRANSLATE",
    val options: List<String> = emptyList(), val hint: String? = null,
    @SerializedName("isCustom") val isCustom: Boolean = false
)
data class AnswerRequest(val userId: Int, val wordId: Int, val answer: String,
                         val exerciseType: String, val responseMs: Int,
                         @SerializedName("isCustom") val isCustom: Boolean = false)
data class AnswerResponse(val correct: Boolean = false, val feedback: String? = null, val correctAnswer: String? = null, val points: Int = 0)
data class ProgressItem(val theme: String = "", val totalAttempts: Int = 0, val correctAttempts: Int = 0, val accuracyPct: Int = 0, val trend: String = "STABLE")
data class RankingItem(val id: Int = 0, val username: String = "", val points: Int = 0, val premium: Boolean = false, val levelEstimated: String = "BEGINNER")
data class DictionaryWord(
    val id: Int = 0, val wordSpa: String = "", val wordEng: String = "",
    val level: String = "BEGINNER", val type: String = "",
    val themes: List<String> = emptyList(),
    val isFalseFriend: Boolean = false, val feedbackHint: String = ""
)
data class WordDefinition(val word: String = "", val definition: String = "", val audioUrl: String = "")


data class CustomCardResponse(
    val id:      Int    = 0,
    val wordSpa: String = "",
    val wordEng: String = "",
    val notes:   String = "",
    val level:   String = "BEGINNER",
    val theme:   String = ""
)

data class CreateCustomCardRequest(
    val userId:  Int,
    val wordSpa: String,
    val wordEng: String,
    val notes:   String = "",
    val level:   String = "BEGINNER",
    val theme:   String = ""
)

data class UpdateSettingsRequest(
    val username:     String? = null,
    val email:        String? = null,
    val password:     String? = null,
    val avatarUrl:    String? = null,
    val learningMode: String? = null
)

data class UpdateCustomCardRequest(
    val wordSpa: String,
    val wordEng: String,
    val notes:   String = "",
    val level:   String = "BEGINNER",
    val theme:   String = ""
)

data class FsrsWord(
    val wordEng:    String = "",
    val wordSpa:    String = "",
    val stability:  Int    = 0,
    val difficulty: Double = 0.0,
    val reps:       Int    = 0,
    val lapses:     Int    = 0,
    val dueDate:    String = ""
)

data class HomeStats(
    val dueToday:        Int    = 0,
    val countNew:        Int    = 0,
    val countLearning:   Int    = 0,
    val countReview:     Int    = 0,
    val countRelearning: Int    = 0,
    val avgStabilityDays: Int   = 0,
    val bestTheme:       String = "",
    val bestAccuracy:    Int    = 0,
    val worstTheme:      String = "",
    val worstAccuracy:   Int    = 0
)

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<UserResponse>

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<UserResponse>

    @GET("api/exercises/session/{userId}")
    suspend fun getSession(@Path("userId") userId: Int): Response<List<WordExercise>>

    @POST("api/exercises/answer")
    suspend fun submitAnswer(@Body body: AnswerRequest): Response<AnswerResponse>

    @POST("api/exercises/session/finish/{userId}")
    suspend fun finishSession(@Path("userId") userId: Int): Response<Map<String, String>>

    @GET("api/progress/{userId}")
    suspend fun getProgress(@Path("userId") userId: Int): Response<List<ProgressItem>>

    @GET("api/ranking")
    suspend fun getRanking(@Query("limit") limit: Int = 20): Response<List<RankingItem>>

    @POST("api/subscription/activate/{userId}")
    suspend fun activatePremium(@Path("userId") userId: Int): Response<Map<String, String>>

    @GET("api/dictionary")
    suspend fun getAllWords(): Response<List<DictionaryWord>>

    @GET("api/dictionary/search")
    suspend fun searchWords(
        @Query("q")     query: String? = null,
        @Query("type")  type: String?  = null,
        @Query("theme") theme: String? = null
    ): Response<List<DictionaryWord>>

    @GET("api/dictionary/types")
    suspend fun getTypes(): Response<List<String>>

    @GET("api/dictionary/themes")
    suspend fun getThemes(): Response<List<String>>

    @GET("api/dictionary/definition/{wordEng}")
    suspend fun getDefinition(@Path("wordEng") wordEng: String): Response<WordDefinition>

    @GET("api/exercises/session/{userId}")
    suspend fun getSessionFiltered(
        @Path("userId") userId: Int,
        @Query("theme") theme: String?  = null,
        @Query("type")  type: String?   = null,
        @Query("source") source: String = "BD",
        @Query("level") level: String?  = null
    ): Response<List<WordExercise>>

    // Custom Cards — usa CustomCardResponse en vez de Map<String, Any>
    @GET("api/custom-cards/{userId}")
    suspend fun getCustomCards(@Path("userId") userId: Int): Response<List<CustomCardResponse>>

    @POST("api/custom-cards")
    suspend fun createCustomCard(@Body body: CreateCustomCardRequest): Response<Map<String, String>>

    @DELETE("api/custom-cards/{cardId}")
    suspend fun deleteCustomCard(@Path("cardId") cardId: Int): Response<Map<String, String>>

    @PUT("api/auth/settings/{userId}")
    suspend fun updateSettings(
        @Path("userId") userId: Int,
        @Body body: UpdateSettingsRequest
    ): Response<UserResponse>

    @PUT("api/custom-cards/{cardId}")
    suspend fun updateCustomCard(
        @Path("cardId") cardId: Int,
        @Body body: UpdateCustomCardRequest
    ): Response<CustomCardResponse>

    @GET("api/home-stats/{userId}")
    suspend fun getHomeStats(@Path("userId") userId: Int): Response<HomeStats>

    @GET("api/home-stats/{userId}/words")
    suspend fun getFsrsWords(
        @Path("userId") userId: Int,
        @Query("state") state: String
    ): Response<List<FsrsWord>>

    // Sugerencias de Merriam — devuelve Map<String, String> para evitar wildcard
    @GET("api/custom-cards/suggestions/{wordSpa}")
    suspend fun getMerriamSuggestions(
        @Path("wordSpa") wordSpa: String
    ): Response<MerriamSuggestionsResponse>
}

data class MerriamSuggestionsResponse(
    val wordSpa:     String       = "",
    val suggestions: List<String> = emptyList(),
    val hasData:     Boolean      = false
)

object RetrofitClient {

    //Para local - Primero correr el .sql, luego ejecutar el Intellj, luego el Android Studio

   private const val BASE_URL = "http://10.0.2.2:8080/"

    // http://10.0.2.2:8080/ para emular en PC con o sin docker

    // Para emular en móvil es la ip de la red, y tanto el PC como el móvil deben
    // de estar conectados AL MISMO WIFI y conectado USB al PC

    //Para Render - servidor backend remoto, comentar el anterior BASE_URL y descomentar el de abajo
   //private const val BASE_URL = "https://compassenglishbackend.onrender.com/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

    val api: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}