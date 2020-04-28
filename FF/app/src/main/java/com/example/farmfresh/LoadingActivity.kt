package com.example.farmfresh

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable


class LoadingActivity : AppCompatActivity(){
    lateinit var featuredListObj : featureLabels
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_page)

        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        val emailHash = token.getString("EMAILHASH","")

        if(emailHash == ""){
            Log.d("LoadingActivity","User Logged In : Starting IndexActivity")
            val loginIntent = Intent(this,LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }

        if ( emailHash!= "" ){
        Log.d("LoadingActivity","Fetching Featured Images from database")
        val ref = FirebaseDatabase.getInstance().getReference("/featured")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("LoadingActivity","Error in Fetching Featured Images : ${p0}")
                return
            }

            override fun onDataChange(p0: DataSnapshot) {
                val featureList = mutableListOf<String>()
                Log.d("LoadingActivity","Successfully Fetched Featured Images ")
                for (name in p0.children){
                    Log.d("LoadingActivity","Image Location : ${name.value.toString()}")
                    featureList.add(name.value.toString())
                }

                featuredListObj = featureLabels(featureList)
                Log.d("LoadingActivity","User Already Logged In :${emailHash}")
                val indexIntent = Intent(this@LoadingActivity, IndexActivity::class.java)
                indexIntent.putExtra("featuredListObj", featuredListObj)
                Log.d("LoadingActivity","User Logged In : Starting IndexActivity")
                startActivity(indexIntent)
                finish()
            }
        })
        }
    }
}


class featureLabels(val featureList: List<String>) : Serializable