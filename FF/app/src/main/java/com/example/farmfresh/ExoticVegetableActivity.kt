package com.example.farmfresh

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class ExoticVegetableActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exoticvegetable)
        val subDataObj = intent.getSerializableExtra("subDataObj") as subCategoryData
        Log.d("ExoticVegetableActivity","${subDataObj.itemList}")
    }
}