package com.example.farmfresh

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class FruitActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fruit)
        val subDataObj = intent.getSerializableExtra("subDataObj") as subCategoryData
        Log.d("FruitActivity","${subDataObj.itemList}")
    }
}