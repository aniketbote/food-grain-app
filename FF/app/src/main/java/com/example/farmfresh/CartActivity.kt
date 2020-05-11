package com.example.farmfresh

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_cart.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

lateinit var cartTotal: TextView

class CartActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartTotal = findViewById(R.id.cart_amount)

        val db = CartDatabase(this)
        val cartList = db.readData()

        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        val emailHash = token.getString("EMAILHASH", "")
        Log.d("CartActivity","$cartList")

        cartTotal.text = HelperUtils.getCost(cartList).toString()

        val recycleView: RecyclerView = findViewById(R.id.cart_list)
        recycleView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false) as RecyclerView.LayoutManager?
        val adapter = CartAdapter(this, cartList)
        recycleView.adapter = adapter






        placeOrder_cart.setOnClickListener {
            if(cartCount == 0){
                Toast.makeText(this,"Nothing in the basket",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
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