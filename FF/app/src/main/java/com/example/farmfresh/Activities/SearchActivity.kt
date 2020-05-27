package com.example.farmfresh.Activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.farmfresh.Adapters.ProductAdapter
import com.example.farmfresh.Adapters.SearchProductAdapter
import com.example.farmfresh.Database.CartDatabase
import com.example.farmfresh.Model.CartItem
import com.example.farmfresh.Model.ProductList
import com.example.farmfresh.Model.SubData
import com.example.farmfresh.R

class SearchActivity: AppCompatActivity() {
    lateinit var cartList: MutableList<CartItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itemlist)
        val subDataObj = intent.getSerializableExtra("subDataObj") as ProductList
        Log.d("ProductActivity", "${subDataObj.itemList}")

       // val type = intent.getStringExtra("type")
       // Log.d("ProductActivity", "$type")
        val db = CartDatabase(this)
        cartList= db.readData()

        val recyclerView: RecyclerView = findViewById(R.id.recycleview)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?
        var adapter = SearchProductAdapter(
            subDataObj.itemList,
            cartList
        )
        recyclerView.adapter = adapter

    }
}