package edu.elveshka.dailyanimal.viewmodel

import edu.elveshka.dailyanimal.api.cats.CatApiClient
import edu.elveshka.dailyanimal.api.dogs.DogApiClient
import edu.elveshka.dailyanimal.api.quotes.QuoteApiClient
import edu.elveshka.dailyanimal.model.CatResponse
import edu.elveshka.dailyanimal.model.DogResponse
import edu.elveshka.dailyanimal.model.QuoteResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private lateinit var viewModel: MainViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val mockDogApiClient = mockk<DogApiClient>()
    private val mockCatApiClient = mockk<CatApiClient>()
    private val mockQuoteApiClient = mockk<QuoteApiClient>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel(mockDogApiClient, mockCatApiClient, mockQuoteApiClient)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleApi should switch between dog and cat API`() = runTest {
        // Initial state should be dog API
        assertEquals(true, viewModel.uiState.value.isDogApi)

        // Toggle to cat API
        viewModel.toggleApi()
        assertEquals(false, viewModel.uiState.value.isDogApi)

        // Toggle back to dog API
        viewModel.toggleApi()
        assertEquals(true, viewModel.uiState.value.isDogApi)
    }

    @Test
    fun `loadImage should update state with dog image when using dog API`() = runTest {
        // Given
        val expectedDogResponse = DogResponse("https://example.com/dog.jpg", "success")
        coEvery { mockDogApiClient.api.getRandomDog() } returns expectedDogResponse

        // When
        viewModel.loadImage()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(expectedDogResponse.message, viewModel.uiState.value.imageUrl)
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `loadImage should update state with cat image when using cat API`() = runTest {
        // Given
        val expectedCatResponse = listOf(CatResponse("1", "https://example.com/cat.jpg", 800, 600))
        coEvery { mockCatApiClient.api.getRandomCats() } returns expectedCatResponse
        viewModel.toggleApi() // Switch to cat API

        // When
        viewModel.loadImage()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(expectedCatResponse.first().url, viewModel.uiState.value.imageUrl)
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `loadImage should handle errors correctly`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { mockDogApiClient.api.getRandomDog() } throws Exception(errorMessage)

        // When
        viewModel.loadImage()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(errorMessage, viewModel.uiState.value.error)
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.imageUrl)
    }

    @Test
    fun `loadImage should load quote after successful image load`() = runTest {
        // Given
        val expectedDogResponse = DogResponse("https://example.com/dog.jpg", "success")
        val expectedQuoteResponse = QuoteResponse("Test quote", "Test author")

        coEvery { mockDogApiClient.api.getRandomDog() } returns expectedDogResponse
        coEvery { mockQuoteApiClient.api.getRandomQuote() } returns expectedQuoteResponse

        // When
        viewModel.loadImage()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(expectedQuoteResponse.quoteText, viewModel.uiState.value.quote)
        assertEquals(expectedQuoteResponse.quoteAuthor, viewModel.uiState.value.quoteAuthor)
    }
} 