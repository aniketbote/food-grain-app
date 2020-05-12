package com.example.farmfresh

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {

    @FormUrlEncoded
    @POST("placeorder")
    fun placeorder(
        @Field("cartList") cartList:JsonObject,
        @Field("emailHash") emailHash:String
    ):Call<PlaceOrderResponse>

    @FormUrlEncoded
    @POST("orderreceived")
    fun orderreceived(
        @Field("emailHash") emailHash:String
    ):Call<PlaceOrderResponse>

}