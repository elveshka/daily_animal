package com.example.dailyanimal.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Dog API
data class DogResponse(
    val message: String,
    val status: String
)

interface DogApiService {
    @GET("breeds/image/random")
    suspend fun getRandomDog(): DogResponse
}

// Cat API
data class CatResponse(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int
)

interface CatApiService {
    @GET("images/search")
    suspend fun getRandomCat(): List<CatResponse>
}

// Quote API
data class QuoteResponse(
    val content: String,
    val author: String
)

interface QuoteApiService {
    @GET("random")
    suspend fun getQuote(): QuoteResponse
}

// API Clients
object DogApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://dog.ceo/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: DogApiService = retrofit.create(DogApiService::class.java)
}

object CatApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.thecatapi.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: CatApiService = retrofit.create(CatApiService::class.java)
}

object QuoteApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.quotable.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: QuoteApiService = retrofit.create(QuoteApiService::class.java)
} 