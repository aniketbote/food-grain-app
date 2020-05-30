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
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity(){
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.d("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.d("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.d("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    private fun checkConnection(context: Context) {
        val isConnected = isOnline(context)
        Log.d("LoadingActivity", "$isConnected")

        if(!isConnected){
            Log.d("LoadingActivity", "No connection : Starting No Connection Activity")
            val noConnectionIntent = Intent(context, NoConnectionActivity::class.java)
            startActivityForResult(noConnectionIntent,999)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        checkConnection(this)

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