package com.example.farmfresh

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        val name = token.getString("name","")
        val email = token.getString("email","")
        val phone = token.getString("phone","")
        val address = token.getString("address","")
        val gender = token.getString("gender","")
        val imageUri = token.getString("imageUri","")
        val birthdate = token.getString("birthdate","")
        Log.d("ProfileActivity","${imageUri}")

        val imageView: CircleImageView = findViewById(R.id.userphoto_profile)
        Glide.with(this).load("${imageUri}").into(imageView)
        Log.d("IndexActivity","Image Loaded On Nav Bar")
    }
}