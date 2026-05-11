package com.compassenglish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.compassenglish.data.api.RetrofitClient
import com.compassenglish.ui.screens.*
import com.compassenglish.ui.theme.CompassEnglishTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompassEnglishTheme {
                CompassNavHost()
            }
        }
    }
}

@Composable
fun CompassNavHost() {
    val navController = rememberNavController()
    val scope         = rememberCoroutineScope()

    var currentUserId   by remember { mutableIntStateOf(-1) }
    var currentUsername by remember { mutableStateOf("") }
    var currentPoints       by remember { mutableIntStateOf(0) }
    var currentLearningMode by remember { mutableStateOf("FREE") }

    fun refreshPoints() {
        if (currentUserId < 0) return
        scope.launch {
            try {
                val resp = RetrofitClient.api.getRanking(200)
                if (resp.isSuccessful) {
                    resp.body()?.find { it.id == currentUserId }
                        ?.let { currentPoints = it.points }
                }
            } catch (_: Exception) {}
        }
    }

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = { userId, username, learningMode ->
                    currentUserId       = userId
                    currentUsername     = username
                    currentLearningMode = learningMode
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { userId, username ->
                    currentUserId   = userId
                    currentUsername = username
                    currentPoints   = 0
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable("home") {
            LaunchedEffect(Unit) { refreshPoints() }

            HomeScreen(
                userId    = currentUserId,
                username  = currentUsername,
                points    = currentPoints,
                // Navega primero a filtros, no directamente a ejercicios
                onNavigateToExercises  = { navController.navigate("exercise-filter") },
                onNavigateToDictionary = { navController.navigate("dictionary") },
                onNavigateToSettings   = { navController.navigate("settings") },
                onNavigateToFsrs       = { state -> navController.navigate("fsrs-detail/$state") },
                onNavigateToRanking    = { navController.navigate("ranking") },
                onNavigateToMyCards    = { navController.navigate("custom-cards") }
            )
        }

        // Pantalla de filtros — previa a los ejercicios
        composable("exercise-filter") {
            ExerciseFilterScreen(
                onBack = { navController.popBackStack() },
                onStart = { config ->
                    // Guarda la config para que ExerciseScreen la lea
                    navController.currentBackStackEntry
                        ?.savedStateHandle?.set("sessionConfig", config)
                    navController.navigate("exercises")
                }
            )
        }

        // Pantalla de ejercicios que lee la config de filtros
        composable("exercises") {
            val config = navController.previousBackStackEntry
                ?.savedStateHandle?.get<SessionConfig>("sessionConfig")
                ?: SessionConfig()

            ExerciseScreen(
                userId        = currentUserId,
                sessionConfig = config,
                onFinish      = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("dictionary") {
            DictionaryScreen(onBack = { navController.popBackStack() })
        }

        // Pantalla de tarjetas propias del usuario
        composable("custom-cards") {
            CustomCardsScreen(
                userId = currentUserId,
                onBack = { navController.popBackStack() }
            )
        }

        composable("ranking") {
            RankingScreen(
                currentUserId = currentUserId,
                onBack        = { navController.popBackStack() }
            )
        }

        composable("fsrs-detail/{state}") { backStack ->
            val state = backStack.arguments?.getString("state") ?: "REVIEW"
            FsrsDetailScreen(
                userId       = currentUserId,
                initialState = state,
                onBack       = { navController.popBackStack() }
            )
        }

        composable("settings") {
            SettingsScreen(
                userId              = currentUserId,
                currentUsername     = currentUsername,
                currentLearningMode = currentLearningMode,
                onBack              = { navController.popBackStack() },
                onUpdated           = { newUsername, newMode ->
                    currentUsername     = newUsername
                    currentLearningMode = newMode
                }
            )
        }
    }
}