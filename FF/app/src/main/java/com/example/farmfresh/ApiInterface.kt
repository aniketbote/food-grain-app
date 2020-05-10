package com.example.farmfresh

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {

    @FormUrlEncoded
    @POST("hello")
    fun hello(
        @Field("cartList") cartList:JsonObject
    ):Call<PlaceOrderResponse>
}