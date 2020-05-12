package com.example.farmfresh

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
        if(cartList.size > 0){
            itemText.visibility = View.VISIBLE
            itemText.text = cartList.size.toString()
            cartCount = cartList.size
        }

        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        val emailHash = token.getString("EMAILHASH", "")
        val pendingOrder = token.getString("pendingOrder", "")
        Log.d("CartActivity","$pendingOrder")
        Log.d("CartActivity","$cartList")

        cartTotal.text = HelperUtils.getCost(cartList).toString()

        val recycleView: RecyclerView = findViewById(R.id.cart_list)
        recycleView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false) as RecyclerView.LayoutManager?
        val adapter = CartAdapter(this, cartList)
        recycleView.adapter = adapter






        placeOrder_cart.setOnClickListener {
            if(cartList.size == 0){
                Toast.makeText(this,"Nothing in the basket",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(pendingOrder == true.toString()){
                Toast.makeText(this,"You have a pending order",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = CartDatabase(this)
            val cartJsonObj = db.readDataJson()
            Log.d("CartActivity","$cartJsonObj")
            Log.d("CartActivity","Pressed Place Order Button")
            RetrofitClient.instance.placeorder(cartJsonObj, emailHash.toString())
                .enqueue(object : Callback<PlaceOrderResponse>{
                    override fun onFailure(call: Call<PlaceOrderResponse>, t: Throwable) {
                        Log.d("CartActivity","Failed : ${t.message}")
                    }

                    override fun onResponse(call: Call<PlaceOrderResponse>, response: Response<PlaceOrderResponse>) {
                        Log.d("CartActivity","Successfull : ${response.body()?.message}")
                        Toast.makeText(this@CartActivity,"${response.body()?.message}",Toast.LENGTH_SHORT).show()
                        if(response.body()?.errorCode == 0){
                            Log.d("CartActivity","Setting shared preferences")
                            val pref = this@CartActivity.getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
                            val editor = pref.edit()
                            editor.putString("pendingOrder", true.toString())
                            editor.commit()
                            for(i in 0 until cartList.size){
                                db.deleteData(cartList[i].name)
                                cartCount = 0
                                itemText.visibility = View.INVISIBLE
                            }
                            startActivity(intent)
                            finish()
                        }

                        if(response.body()?.errorCode == 1){
                            val deficientItems = response.body()!!.deficiency.split(',')
                            Log.d("CartActivity","${deficientItems}")
                            for( i in 0 until deficientItems.size-1){
                                db.deleteData(deficientItems[i])
                            }
                            startActivity(intent)
                            finish()
                        }
                    }

                })
        }

    }
}