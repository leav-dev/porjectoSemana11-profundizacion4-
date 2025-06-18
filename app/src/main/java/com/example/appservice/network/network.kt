package com.example.appservice.network

import com.example.appservice.model.Post
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("post") suspend fun getPosts(): Response<List<Post>>
}