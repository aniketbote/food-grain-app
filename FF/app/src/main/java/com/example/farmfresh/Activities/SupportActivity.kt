package com.example.farmfresh.Activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.farmfresh.R
import com.example.farmfresh.Utilities.HelperUtils
import com.example.farmfresh.Utilities.HelperUtils.checkConnection

class SupportActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)
        HelperUtils.checkConnection(this)
    }
}