package com.example.farmfresh

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest

class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        val emailHash = token.getString("EMAILHASH","")
        if ( emailHash!= "" ){
            Log.d("LoginActivity","User Already Logged In :${emailHash}")
            val intent = Intent(this, IndexActivity::class.java)
            Log.d("LoginActivity","User Logged In : Starting IndexActivity")
            startActivity(intent)
            finish()
        }
        login_login.setOnClickListener {
            Log.d("LoginActivity","Login Button Pressed")
            performLogin()
        }
    }

    private fun performLogin(){
        Log.d("LoginActivity","Inside perFormLogin")
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
                Log.d("LoginActivity","Login Successful : ${it}")
                val emHash = generatehash(email)
                val ref = FirebaseDatabase.getInstance().getReference("/users/${emHash}")
                ref.addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("IndexActivity", "Error During Data Fetching : ${p0}")
                        return
                    }
                    override fun onDataChange(p0: DataSnapshot) {
                        Log.d("IndexActivity","Data Fetched Successfully: ${p0.value}")
                        val email = p0.child("email").value.toString()
                        val phone = p0.child("phone").value.toString()
                        val address = p0.child("address").value.toString()
                        val name = p0.child("name").value.toString()
                        val gender = p0.child("gender").value.toString()
                        val imageUri = p0.child("imageUri").value.toString()
                        val birthdate = p0.child("birthdate").value.toString()

//                        val userInfo = userData(name, email, phone, address, birthdate, gender, imageUri)
//                        Log.d("IndexActivity","userInfo Object= ${userInfo}")

                        val pref = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
                        val editor = pref.edit()
                        editor.putString("EMAILHASH",emHash)
                        editor.putString("email",email)
                        editor.putString("phone",phone)
                        editor.putString("address",address)
                        editor.putString("name",name)
                        editor.putString("gender",gender)
                        editor.putString("imageUri",imageUri)
                        editor.putString("birthdate",birthdate)
                        editor.commit()
                        Log.d("LoginActivity","User Info hash stored in Shared preferences")

                        val intent:Intent = Intent(this@LoginActivity, IndexActivity::class.java)
//                        intent.putExtra("email",email)
//                        intent.putExtra("phone",phone)
//                        intent.putExtra("address",address)
//                        intent.putExtra("name",name)
//                        intent.putExtra("gender",gender)
//                        intent.putExtra("imageUri",imageUri)
//                        intent.putExtra("birthdate",birthdate)

                        Log.d("LoginActivity","Starting IndexActivity")
                        Toast.makeText(this@LoginActivity,"Login Successful",Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                        finish()
                    }
                })
            }
            .addOnFailureListener {
                Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT).show()
                Log.d("LoginActivity","Login Failed : ${it.message}")
            }
    }

    private fun generatehash(stringToBeHashed:String): String {
        val bytes = stringToBeHashed.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

}


//class userData(val name:String, val email:String, val phone:String, val address:String, val birthdate:String, val gender:String, val imageUri: String)