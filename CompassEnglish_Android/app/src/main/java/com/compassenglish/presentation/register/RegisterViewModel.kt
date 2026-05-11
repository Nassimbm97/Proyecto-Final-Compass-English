package com.compassenglish.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compassenglish.domain.model.*
import com.compassenglish.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String = "",
    val user: User? = null
)

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(
        username: String, password: String,
        confirmPassword: String, email: String, learningMode: String = "FREE"
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = "") }
            when (val result = registerUseCase(username, password, confirmPassword, email, learningMode)) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, user = result.data) }
                is Result.Error   -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                else -> Unit
            }
        }
    }
}