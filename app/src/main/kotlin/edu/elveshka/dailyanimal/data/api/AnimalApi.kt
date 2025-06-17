package edu.elveshka.dailyanimal.data.api

import retrofit2.http.GET

interface AnimalApi {
    @GET("breeds/image/random")
    suspend fun getRandomDog(): DogResponse

    @GET("images/search")
    suspend fun getRandomCat(): List<CatResponse>
}

data class DogResponse(
    val message: String,
    val status: String
)

data class CatResponse(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int
) 