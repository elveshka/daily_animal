package edu.elveshka.dailyanimal.api.quotes

import edu.elveshka.dailyanimal.model.QuoteResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface QuoteApi {
    @GET("api/1.0/")
    suspend fun getRandomQuote(
        @Query("method") method: String = "getQuote",
        @Query("format") format: String = "json",
        @Query("lang") lang: String = "ru"
    ): QuoteResponse
} 