package com.example.farmfresh

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        val name = token.getString("name","")
        val email = token.getString("email","")
        val phone = token.getString("phone","")
        val address = token.getString("address","")
        val gender = token.getString("gender","")
        val imageUri = token.getString("imageUri","")
        val birthdate = token.getString("birthdate","")
    }
}