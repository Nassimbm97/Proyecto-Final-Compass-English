package com.compassenglish.presentation.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compassenglish.data.api.RetrofitClient
import com.compassenglish.domain.model.*
import com.compassenglish.domain.usecase.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class DictionaryUiState(
    val isLoading: Boolean = true,
    val words: List<WordEntry> = emptyList(),
    val selectedWord: WordEntry? = null,
    val wordDetail: WordDetail? = null,
    val isLoadingDetail: Boolean = false,
    val error: String = "",
    // Filtros
    val availableTypes: List<String> = emptyList(),
    val availableThemes: List<String> = emptyList(),
    val selectedType: String? = null,
    val selectedTheme: String? = null,
    val searchQuery: String = ""
)

class DictionaryViewModel(
    private val getAllWordsUseCase: GetAllWordsUseCase,
    private val searchWordsUseCase: SearchWordsUseCase,
    private val getWordDetailUseCase: GetWordDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DictionaryUiState())
    val uiState: StateFlow<DictionaryUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadAllWords()
        loadFilters()
    }

    private fun loadAllWords() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getAllWordsUseCase()) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, words = result.data) }
                is Result.Error   -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                else -> Unit
            }
        }
    }

    private fun loadFilters() {
        viewModelScope.launch {
            try {
                val types  = RetrofitClient.api.getTypes()
                val themes = RetrofitClient.api.getThemes()
                _uiState.update {
                    it.copy(
                        availableTypes  = types.body()  ?: emptyList(),
                        availableThemes = themes.body() ?: emptyList()
                    )
                }
            } catch (_: Exception) {}
        }
    }

    fun search(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        triggerSearch()
    }

    fun setTypeFilter(type: String?) {
        _uiState.update { it.copy(selectedType = if (it.selectedType == type) null else type) }
        triggerSearch()
    }

    fun setThemeFilter(theme: String?) {
        _uiState.update { it.copy(selectedTheme = if (it.selectedTheme == theme) null else theme) }
        triggerSearch()
    }

    private fun triggerSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            val state = _uiState.value
            try {
                val resp = RetrofitClient.api.searchWords(
                    query = state.searchQuery.ifBlank { null },
                    type  = state.selectedType,
                    theme = state.selectedTheme
                )
                if (resp.isSuccessful) {
                    val words = resp.body()?.map { dto ->
                        WordEntry(
                            id = dto.id, wordSpa = dto.wordSpa, wordEng = dto.wordEng,
                            level = dto.level, type = dto.type, themes = dto.themes,
                            isFalseFriend = dto.isFalseFriend, feedbackHint = dto.feedbackHint
                        )
                    } ?: emptyList()
                    _uiState.update { it.copy(words = words) }
                }
            } catch (_: Exception) {}
        }
    }

    fun selectWord(word: WordEntry) {
        _uiState.update { it.copy(selectedWord = word, wordDetail = null, isLoadingDetail = true) }
        viewModelScope.launch {
            when (val result = getWordDetailUseCase(word.wordEng)) {
                is Result.Success -> _uiState.update { it.copy(wordDetail = result.data, isLoadingDetail = false) }
                is Result.Error   -> _uiState.update { it.copy(isLoadingDetail = false) }
                else -> Unit
            }
        }
    }

    fun clearSelection() = _uiState.update { it.copy(selectedWord = null, wordDetail = null) }
}
