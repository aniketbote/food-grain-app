package com.example.farmfresh

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class PreviousOrdersActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous)

        val orderListObj = intent.getSerializableExtra("orderListObj") as OrderList
        Log.d("CurrentActivity","${orderListObj.orderList}")
    }
}