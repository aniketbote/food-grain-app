package com.example.farmfresh

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        register_registration.setOnClickListener {
            perfromRegistration()
        }

        login_registration.setOnClickListener {
            Log.d( "MainActivity","On clicking Login")
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        uploadphoto_registration.setOnClickListener {
            Log.d("MainActivity","Clicked Upload Photo")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("MainActivity", "Photo Selected")
            val uri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            uploadphoto_registration.setBackgroundDrawable(bitmapDrawable)
        }

    }

    private fun perfromRegistration(){
        val name = name_registration.text.toString()
        val email = email_registration.text.toString()
        val phone = phone_registration.text.toString()
        val address = address_registration.text.toString()
        val birthdate = birthdate_registration.text.toString()
        val password = password_registration.text.toString()


        //Sanity Checks : Empty
        if (name.isEmpty()){
            Toast.makeText(this,"Name field cannot be empty",Toast.LENGTH_SHORT).show()
            return
        }
        if (email.isEmpty()){
            Toast.makeText(this,"Email field cannot be empty",Toast.LENGTH_SHORT).show()
            return
        }
        if (phone.isEmpty()){
            Toast.makeText(this,"Phone field cannot be empty",Toast.LENGTH_SHORT).show()
            return
        }
        if (address.isEmpty()){
            Toast.makeText(this,"Phone field cannot be empty",Toast.LENGTH_SHORT).show()
            return
        }
        if (birthdate.isEmpty()){
            Toast.makeText(this,"Birthdate field cannot be empty",Toast.LENGTH_SHORT).show()
            return
        }
        if (password.isEmpty()){
            Toast.makeText(this,"Password field cannot be empty",Toast.LENGTH_SHORT).show()
            return
        }


        //Sanity Checks : Email
        if(!EmailValidate(email)){
            Toast.makeText(this,"Enter a valid Email",Toast.LENGTH_SHORT).show()
            return
        }

        //Sanity Checks : Password Length
        if (password.length < 6 || password.length > 20){
            Toast.makeText(this,"Password should contain 6 to 20 characters",Toast.LENGTH_SHORT).show()
            return
        }

        //Sanity Checks : Phone Length
        if (phone.length != 10){
            Toast.makeText(this,"Enter Valid Phone Number",Toast.LENGTH_SHORT).show()
            return
        }



        //Firbase Auth Object
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful){
                    return@addOnCompleteListener
                }
                Toast.makeText(this,"Registration Succesful",Toast.LENGTH_SHORT).show()
                Log.d("MainActivity","Registration Succesful : ${it}")
                setContentView(R.layout.activity_login)

            }
            .addOnFailureListener {
                Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT).show()
                Log.d("MainActivity","Registration Failed : ${it.message}")
            }


    }


    private fun EmailValidate(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.toRegex().matches(email)
        }
}
