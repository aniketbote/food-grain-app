package com.example.farmfresh

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class ExoticFruitActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exoticfruit)
        val subDataObj = intent.getSerializableExtra("subDataObj") as subCategoryData
        Log.d("ExoticFruitActivity","${subDataObj.itemList}")
    }
}