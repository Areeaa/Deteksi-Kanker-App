package com.dicoding.asclepius.remote.retrofit

import com.dicoding.asclepius.remote.response.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines?q=cancer&category=health&language=en")
    suspend fun getNews(@Query("apiKey") apiKey: String): NewsResponse
}