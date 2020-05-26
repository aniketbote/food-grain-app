package com.example.farmfresh.Retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
//    private const val BASE_URL = "http://192.168.29.242:5000/"
    private const val BASE_URL = "http://192.168.0.3:5000/"
    val instance: ApiInterface by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiInterface::class.java)
    }

}