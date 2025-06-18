package com.example.appservice.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitlInstance {
    private const val Base_Url = "https://jsonplaceholder.typecode.com/"
    private val okHttpClient =  OkHttpClient.Builder().build()

    private val retrofit by lazy{
        Retrofit.Builder()
            .baseUrl(Base_Url).client(okHttpClient)
            .addConverterFactory((GsonConverterFactory.create())).build()
    }

    val apiService: ApiService by lazy{
        retrofit.create(ApiService::class.java)
    }
}