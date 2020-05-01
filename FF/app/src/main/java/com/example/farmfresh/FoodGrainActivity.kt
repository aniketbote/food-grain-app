package com.example.farmfresh

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class FoodGrainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foodgrain)
        val subDataObj = intent.getSerializableExtra("subDataObj") as subCategoryData
        Log.d("FoodGrainActivity","${subDataObj.itemList}")
    }
}