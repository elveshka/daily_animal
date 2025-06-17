package edu.elveshka.dailyanimal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.elveshka.dailyanimal.api.cats.CatApiClient
import edu.elveshka.dailyanimal.api.cats.CatRepository
import edu.elveshka.dailyanimal.api.dogs.DogApiClient
import edu.elveshka.dailyanimal.api.dogs.DogRepository
import edu.elveshka.dailyanimal.api.quotes.QuoteApiClient
import edu.elveshka.dailyanimal.api.quotes.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val imageUrl: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDogApi: Boolean = true,
    val quote: String? = null,
    val quoteAuthor: String? = null,
    val showTooltip: Boolean = false,
    val hasShownFirstTooltip: Boolean = false
)

class MainViewModel(
    private val dogApiClient: DogApiClient,
    private val catApiClient: CatApiClient,
    private val quoteApiClient: QuoteApiClient
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val dogRepository = DogRepository(dogApiClient)
    private val catRepository = CatRepository(catApiClient)
    private val quoteRepository = QuoteRepository(quoteApiClient)

    fun toggleApi() {
        _uiState.value = _uiState.value.copy(
            isDogApi = !_uiState.value.isDogApi,
            imageUrl = null,
            showTooltip = false
        )
    }

    fun loadImage() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                quote = null,
                quoteAuthor = null
            )

            try {
                val imageUrl = if (_uiState.value.isDogApi) {
                    dogRepository.getRandomDog().message
                } else {
                    catRepository.getRandomCats().firstOrNull()?.url
                        ?: throw Exception("No cat image found")
                }

                _uiState.value = _uiState.value.copy(
                    imageUrl = imageUrl,
                    isLoading = false
                )

                loadQuote()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    private fun loadQuote() {
        viewModelScope.launch {
            try {
                val quoteResponse = quoteRepository.getRandomQuote()
                _uiState.value = _uiState.value.copy(
                    quote = quoteResponse.quoteText,
                    quoteAuthor = quoteResponse.quoteAuthor
                )

                if (!_uiState.value.hasShownFirstTooltip) {
                    _uiState.value = _uiState.value.copy(
                        hasShownFirstTooltip = true,
                        showTooltip = true
                    )
                    kotlinx.coroutines.delay(6000)
                    _uiState.value = _uiState.value.copy(showTooltip = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    quote = "Уга-буга!",
                    quoteAuthor = "Elvek Goriaev"
                )
            }
        }
    }
} 