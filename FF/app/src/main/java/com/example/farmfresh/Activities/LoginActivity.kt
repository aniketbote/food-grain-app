package com.example.farmfresh.Activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.farmfresh.Utilities.HelperUtils
import com.example.farmfresh.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(){
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
        setContentView(R.layout.activity_login)
        checkConnection(this)

        registration_login.setOnClickListener {
            Log.d("LoginActivity","Clicked Register Button")
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
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

        //Dialog - Progress Bar
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_bar,null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val message = dialogView.findViewById<TextView>(R.id.text_progressBar)
        message.text = "Logging In"
        val dialog = builder.create()
        dialog.show()

        // Firebase Sign in
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful){
                    return@addOnCompleteListener
                }
                Log.d("LoginActivity","Login Successful : ${it}")
                val emHash =
                    HelperUtils.generatehash(email)
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
                        val imageUri = p0.child("imageUri").value.toString()


                        val pref = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
                        val editor = pref.edit()
                        editor.putString("EMAILHASH",emHash)
                        editor.putString("email",email)
                        editor.putString("phone",phone)
                        editor.putString("address",address)
                        editor.putString("name",name)
                        editor.putString("imageUri",imageUri)
                        editor.commit()
                        Log.d("LoginActivity","User Info hash stored in Shared preferences")

                        val loadingIntent:Intent = Intent(this@LoginActivity, LoadingActivity::class.java)
                        Log.d("LoginActivity","Starting LoadingActivity")
                        Toast.makeText(this@LoginActivity,"Login Successful",Toast.LENGTH_SHORT).show()
                        loadingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(loadingIntent)
                        dialog.dismiss()
                        finish()
                    }
                })
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT).show()
                Log.d("LoginActivity","Login Failed : ${it.message}")
            }
    }

}

