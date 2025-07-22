package edu.elveshka.dailyanimal.data.api

import edu.elveshka.dailyanimal.domain.model.Quote
import retrofit2.http.GET
import retrofit2.http.Query

interface QuoteApi {
    @GET(".")
    suspend fun getRandomQuote(
        @Query("method") method: String = "getQuote",
        @Query("format") format: String = "json",
        @Query("lang") lang: String = "ru",
        @Query("_") timestamp: Long = System.currentTimeMillis()
    ): Quote
}