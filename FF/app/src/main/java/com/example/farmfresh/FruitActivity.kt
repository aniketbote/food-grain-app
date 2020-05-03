package com.example.farmfresh

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FruitActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itemlist)
        val subDataObj = intent.getSerializableExtra("subDataObj") as SubData
        Log.d("FruitActivity","${subDataObj.itemList}")

        var recyclerView: RecyclerView = findViewById(R.id.recycleview)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        var adapter= ProductAdapter(subDataObj.itemList)
        recyclerView.adapter = adapter
    }
}