package com.example.farmfresh

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_cart.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        val db = CartDatabase(this)
        val cartList = db.readData()

        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        val emailHash = token.getString("EMAILHASH", "")
        Log.d("CartActivity","$cartList")



        placeOrder_cart.setOnClickListener {
            val db = CartDatabase(this)
            val cartJsonObj = db.readDataJson()
            Log.d("CartActivity","$cartJsonObj")
            Log.d("CartActivity","Pressed Place Order Button")
            RetrofitClient.instance.hello(cartJsonObj, emailHash.toString())
                .enqueue(object : Callback<PlaceOrderResponse>{
                    override fun onFailure(call: Call<PlaceOrderResponse>, t: Throwable) {
                        Log.d("CartActivity","Failed : ${t.message}")
                    }

                    override fun onResponse(call: Call<PlaceOrderResponse>, response: Response<PlaceOrderResponse>) {
                        Log.d("CartActivity","Successfull : ${response.body()?.hello}")
                    }

                })
        }
    }
}