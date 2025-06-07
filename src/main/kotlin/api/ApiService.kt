package api

import model.DogResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// This interface will be updated once you provide the actual API details
interface ApiService {
    @GET("api/breeds/image/random")
    suspend fun getRandomDog(): DogResponse
}

object ApiClient {
    private const val BASE_URL = "https://dog.ceo/"
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        
    val service: ApiService = retrofit.create(ApiService::class.java)
} 