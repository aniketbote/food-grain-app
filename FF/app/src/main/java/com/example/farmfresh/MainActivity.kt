package com.example.farmfresh

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        register_registration.setOnClickListener {
            val name = name_registration.text.toString()
            val email = email_registration.text.toString()
            val phone = phone_registration.text.toString()
            val address = address_registration.text.toString()
            val birthdate = birthdate_registration.text.toString()
            val password = password_registration.text.toString()

            Log.d( "MainActivity", "\nName = " +name+"\nEmail = "+email+"\nPhone = "+phone+"\nAddress = "+address+"\nBirthday = "+birthdate+"\nPassword = "+password+"\n")
        }

        login_registration.setOnClickListener {
            Log.d( "MainActivity","On clicking Login")
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }



    }
}
