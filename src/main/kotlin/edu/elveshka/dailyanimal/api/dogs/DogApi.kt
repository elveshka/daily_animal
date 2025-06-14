package edu.elveshka.dailyanimal.api.dogs

import edu.elveshka.dailyanimal.model.DogResponse
import retrofit2.http.GET

interface DogApi {
    @GET("breeds/image/random")
    suspend fun getRandomDog(): DogResponse
} 