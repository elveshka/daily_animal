package edu.elveshka.dailyanimal.api.quotes

import edu.elveshka.dailyanimal.model.QuoteResponse

class QuoteRepository(private val apiClient: QuoteApiClient) {
    suspend fun getRandomQuote(): QuoteResponse {
        return apiClient.api.getRandomQuote()
    }
} 