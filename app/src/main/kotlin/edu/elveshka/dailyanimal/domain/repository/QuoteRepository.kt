package edu.elveshka.dailyanimal.domain.repository

import edu.elveshka.dailyanimal.domain.model.Quote
import kotlinx.coroutines.flow.Flow

interface QuoteRepository {
    suspend fun getRandomQuote(): Quote
    fun getQuoteHistory(): Flow<List<Quote>>
    suspend fun saveQuote(quote: Quote)
} 