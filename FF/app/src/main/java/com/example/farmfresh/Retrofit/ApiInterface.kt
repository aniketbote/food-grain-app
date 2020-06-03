package com.example.farmfresh.Retrofit

import com.example.farmfresh.Model.BraintreeResponse
import com.example.farmfresh.Model.PlaceOrderResponse
import com.example.farmfresh.Model.Product
import com.example.farmfresh.Model.ProductList
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {

    @FormUrlEncoded
    @POST("placeorder")
    fun placeorder(
        @Field("cartList") cartList:JsonObject,
        @Field("emailHash") emailHash:String,
        @Field("address") address:String
    ):Call<PlaceOrderResponse>

    @FormUrlEncoded
    @POST("orderreceived")
    fun orderreceived(
        @Field("emailHash") emailHash:String
    ):Call<PlaceOrderResponse>

    @FormUrlEncoded
    @POST("search")
    fun search(
        @Field("pattern") pattern:String
    ):Call<ProductList>

    @FormUrlEncoded
    @POST("checkout")
    fun checkout(
        @Field("amount") amount:String,
        @Field("nonce") nonce:String
    ):Call<BraintreeResponse>

    @GET("client_token")
    fun client_token():Call<String>

    @POST("popular")
    fun popular():Call<MutableList<Product>>

}