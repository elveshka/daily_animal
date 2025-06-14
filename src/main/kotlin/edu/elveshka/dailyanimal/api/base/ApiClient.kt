package edu.elveshka.dailyanimal.api.base

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class ApiClient<T> {
    protected abstract val baseUrl: String
    protected abstract val apiClass: Class<T>

    protected val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: T by lazy {
        retrofit.create(apiClass)
    }
} 