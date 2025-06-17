package edu.elveshka.dailyanimal.api.dogs

import edu.elveshka.dailyanimal.api.base.ApiClient

class DogApiClient : ApiClient<DogApi>() {
    override val baseUrl: String = "https://dog.ceo/api/"
    override val apiClass: Class<DogApi> = DogApi::class.java
} 