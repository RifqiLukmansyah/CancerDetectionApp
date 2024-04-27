package com.dicoding.cancerdetectionapp.data.retrofit

import com.dicoding.cancerdetectionapp.BuildConfig
import com.dicoding.cancerdetectionapp.data.response.Article
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines")
    suspend fun getArticle(
        @Query("q") country: String = "cancer",
        @Query("category") category: String = "health",
        @Query("language") language: String = "en",
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY
    ): Article
}