package com.example.farmfresh

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        login_login.setOnClickListener {
            Log.d("LoginActivity","Login Button Pressed")
            performLogin()
        }
    }

    private fun performLogin(){
        val email = email_login.text.toString()
        val password = password_login.text.toString()

        // Sanity Checks : Empty
        if (email.isEmpty()){
            Toast.makeText(this,"Email field cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.isEmpty()){
            Toast.makeText(this,"Password field cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Firebase Sign in
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful){
                    return@addOnCompleteListener
                }
                Toast.makeText(this,"Login Successful",Toast.LENGTH_SHORT).show()
                Log.d("LoginActivity","Login Succesful : ${it}")
            }
            .addOnFailureListener {
                Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT).show()
                Log.d("LoginActivity","Login Falied : ${it.message}")
            }
    }

}
