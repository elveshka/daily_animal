package edu.elveshka.dailyanimal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.elveshka.dailyanimal.data.api.AnimalApi
import edu.elveshka.dailyanimal.data.api.ApiConfig
import edu.elveshka.dailyanimal.data.api.CatResponse
import edu.elveshka.dailyanimal.data.api.DogResponse
import edu.elveshka.dailyanimal.data.api.QuoteApi
import edu.elveshka.dailyanimal.data.repository.DailyContentRepositoryImpl
import edu.elveshka.dailyanimal.domain.model.ContentType
import edu.elveshka.dailyanimal.domain.repository.DailyContentRepository
import edu.elveshka.dailyanimal.domain.usecase.GetRandomContentUseCase
import edu.elveshka.dailyanimal.presentation.state.DailyContentUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DailyContentViewModel : ViewModel() {
    private var isFirstLoad = true
    private var hasShownTooltip = false

    private val _uiState = MutableStateFlow(DailyContentUiState(
        showTooltip = false,
        contentType = ContentType.CAT
    ))
    val uiState: StateFlow<DailyContentUiState> = _uiState.asStateFlow()

    private val dogRetrofit = Retrofit.Builder()
        .baseUrl(ApiConfig.DOG_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val catRetrofit = Retrofit.Builder()
        .baseUrl(ApiConfig.CAT_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val quoteRetrofit = Retrofit.Builder()
        .baseUrl(ApiConfig.QUOTE_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val animalApi = object : AnimalApi {
        override suspend fun getRandomDog(): DogResponse {
            return dogRetrofit.create(AnimalApi::class.java).getRandomDog()
        }

        override suspend fun getRandomCat(): List<CatResponse> {
            return catRetrofit.create(AnimalApi::class.java).getRandomCat()
        }
    }

    private val quoteApi = quoteRetrofit.create(QuoteApi::class.java)
    private val repository: DailyContentRepository = DailyContentRepositoryImpl(animalApi, quoteApi)
    private val getRandomContentUseCase = GetRandomContentUseCase(repository)

    init {
        loadContent()
    }

    fun toggleContentType() {
        hasShownTooltip = true
        _uiState.update { currentState ->
            currentState.copy(
                contentType = if (currentState.contentType == ContentType.DOG) ContentType.CAT else ContentType.DOG,
                showTooltip = false,
                content = null
            )
        }
        loadContent()
    }

    fun hideTooltip() {
        hasShownTooltip = true
        _uiState.update { it.copy(showTooltip = false) }
    }

    fun loadContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val content = getRandomContentUseCase(_uiState.value.contentType)
                _uiState.update { currentState ->
                    currentState.copy(
                        content = content,
                        isLoading = false,
                        showTooltip = !isFirstLoad && !hasShownTooltip
                    )
                }
                isFirstLoad = false
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
} 