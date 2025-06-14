package edu.elveshka.dailyanimal.api.cats

import edu.elveshka.dailyanimal.model.CatResponse

class CatRepository(private val apiClient: CatApiClient) {
    suspend fun getRandomCats(): List<CatResponse> {
        return apiClient.api.getRandomCats()
    }
} 