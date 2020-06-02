package com.example.farmfresh.Activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.farmfresh.R
import com.example.farmfresh.Utilities.HelperUtils
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        HelperUtils.checkConnection(this)

        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        val name = token.getString("name","")
        val email = token.getString("email","")
        val phone = token.getString("phone","")
        val address = token.getString("address","")
        val gender = token.getString("gender","")
        val imageUri = token.getString("imageUri","")
        val birthdate = token.getString("birthdate","")
        Log.d("ProfileActivity","${imageUri}")

        val constraintLayout: LinearLayout = findViewById(R.id.linearLayout_profile)
        val imageView: CircleImageView = constraintLayout.findViewById(R.id.userphoto_profile)

        Glide.with(this).load("${imageUri}").into(imageView)
        Log.d("IndexActivity","Image Loaded On Nav Bar")

        val useraddress: TextView = constraintLayout.findViewById(R.id.useraddress_profile)
        useraddress.text = address

        val username: TextView = constraintLayout.findViewById(R.id.username_profile)
        username.text = name

        val useremail: TextView = constraintLayout.findViewById(R.id.useremail_profile)
        useremail.text = email

        val userphone: TextView = constraintLayout.findViewById(R.id.userphone_profile)
        userphone.text = phone


    }
}