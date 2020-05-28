package com.example.farmfresh.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.farmfresh.Adapters.CartAdapter
import com.example.farmfresh.Database.CartDatabase
import com.example.farmfresh.Model.OrderList
import com.example.farmfresh.R
import kotlinx.android.synthetic.main.activity_placeorder.*

class PlaceOrderActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placeorder)


        val db = CartDatabase(this)
        val cartList = db.readData()
        if (cartList.size > 0) {
            itemText.visibility = View.VISIBLE
            itemText.text = cartList.size.toString()
            cartCount = cartList.size
        }

        val orderListObj = intent.getSerializableExtra("orderListObj") as OrderList

        val recycleView: RecyclerView = findViewById(R.id.order_list)
        recycleView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?
        val adapter = CartAdapter(this, cartList)
        recycleView.adapter = adapter

        select_address.setOnClickListener{
            Log.d("Order Activity","Clicked address")
            val addressIntent = Intent(this,
                AddAutoActivity::class.java)
            startActivityForResult(addressIntent, 12)
        }



        place_order.setOnClickListener {


            }
        }

    }
