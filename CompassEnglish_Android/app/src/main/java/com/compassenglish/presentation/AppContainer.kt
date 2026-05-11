package com.compassenglish.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.compassenglish.data.api.RetrofitClient
import com.compassenglish.data.repository.*
import com.compassenglish.domain.usecase.*
import com.compassenglish.presentation.dictionary.DictionaryViewModel
import com.compassenglish.presentation.exercise.ExerciseViewModel
import com.compassenglish.presentation.login.LoginViewModel
import com.compassenglish.presentation.register.RegisterViewModel


//  Inyección de dependencias manual
// ApiService a Repository a UseCase a ViewModel


object AppContainer {
    // Data layer
    private val api = RetrofitClient.api
    val authRepo       = AuthRepositoryImpl(api)
    val exerciseRepo   = ExerciseRepositoryImpl(api)
    val dictionaryRepo = DictionaryRepositoryImpl(api)
    val progressRepo   = ProgressRepositoryImpl(api)
    val rankingRepo    = RankingRepositoryImpl(api)

    // Use cases
    val loginUseCase       = LoginUseCase(authRepo)
    val registerUseCase    = RegisterUseCase(authRepo)
    val getSessionUseCase  = GetSessionUseCase(exerciseRepo)
    val submitAnswerUseCase = SubmitAnswerUseCase(exerciseRepo)
    val finishSessionUseCase = FinishSessionUseCase(exerciseRepo)
    val getAllWordsUseCase  = GetAllWordsUseCase(dictionaryRepo)
    val searchWordsUseCase = SearchWordsUseCase(dictionaryRepo)
    val getWordDetailUseCase = GetWordDetailUseCase(dictionaryRepo)
    val getProgressUseCase = GetProgressUseCase(progressRepo)
    val getRankingUseCase  = GetRankingUseCase(rankingRepo)
}

// Factory para cada ViewModel
class LoginViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        LoginViewModel(AppContainer.loginUseCase) as T
}

class RegisterViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        RegisterViewModel(AppContainer.registerUseCase) as T
}

class ExerciseViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ExerciseViewModel(
            AppContainer.getSessionUseCase,
            AppContainer.submitAnswerUseCase,
            AppContainer.finishSessionUseCase
        ) as T
}

class DictionaryViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        DictionaryViewModel(
            AppContainer.getAllWordsUseCase,
            AppContainer.searchWordsUseCase,
            AppContainer.getWordDetailUseCase
        ) as T
}
