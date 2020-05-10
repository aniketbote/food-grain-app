package com.example.farmfresh

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class CartActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val db = CartDatabase(this)
        val cartList = db.readData()
        Log.d("CartActivity","$cartList")
    }
}