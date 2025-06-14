package edu.elveshka.dailyanimal.api.quotes

import edu.elveshka.dailyanimal.api.base.ApiClient

class QuoteApiClient : ApiClient<QuoteApi>() {
    override val baseUrl: String = "https://api.forismatic.com/"
    override val apiClass: Class<QuoteApi> = QuoteApi::class.java
} 