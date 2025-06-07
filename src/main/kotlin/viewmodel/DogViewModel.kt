package viewmodel

import api.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DogViewModel(
    private val apiService: ApiService,
    private val coroutineScope: CoroutineScope
) {
    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _fetchCount = MutableStateFlow(0)
    val fetchCount: StateFlow<Int> = _fetchCount.asStateFlow()

    fun fetchRandomDog() {
        _uiState.value = UiState.Loading
        coroutineScope.launch {
            try {
                val response = apiService.getRandomDog()
                _uiState.value = UiState.Success(response.message)
                _fetchCount.value += 1
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    sealed class UiState {
        object Initial : UiState()
        object Loading : UiState()
        data class Success(val imageUrl: String) : UiState()
        data class Error(val message: String) : UiState()
    }
} 