package edu.elveshka.dailyanimal.api.dogs

import edu.elveshka.dailyanimal.model.DogResponse

class DogRepository(private val apiClient: DogApiClient) {
    suspend fun getRandomDog(): DogResponse {
        return apiClient.api.getRandomDog()
    }
} 