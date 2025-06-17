package edu.elveshka.dailyanimal.api.cats

import edu.elveshka.dailyanimal.model.CatResponse
import retrofit2.http.GET

interface CatApi {
    @GET("images/search?limit=10")
    suspend fun getRandomCats(): List<CatResponse>
} 