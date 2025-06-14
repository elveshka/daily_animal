package edu.elveshka.dailyanimal.api.cats

import edu.elveshka.dailyanimal.api.base.ApiClient

class CatApiClient : ApiClient<CatApi>() {
    override val baseUrl: String = "https://api.thecatapi.com/v1/"
    override val apiClass: Class<CatApi> = CatApi::class.java
} 