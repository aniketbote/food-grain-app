package com.example.farmfresh.Activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.farmfresh.Model.AllData
import com.example.farmfresh.Model.Product
import com.example.farmfresh.Utilities.HelperUtils
import com.example.farmfresh.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.HashMap

var cartCount:Int = 0
class LoadingActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_page)
        HelperUtils.checkConnection(this)

        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        val emailHash = token.getString("EMAILHASH", "")
        val refFeatured =
            FirebaseDatabase.getInstance().getReference("/all_orders/${emailHash}/current")
        refFeatured.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("LoadingActivity", "Error in Fetching all items data : ${p0.message}")
                return
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("LoadingActivity", "Successfully fetched current_orders : ${p0.value}")
                val pref = getSharedPreferences("$emailHash", Context.MODE_PRIVATE)
                val editor = pref.edit()
                if (p0.value != null) {
                    Log.d("LoadingActivity", "current_orders : ${p0.value}")
                    editor.putString("pendingOrder", true.toString())
                    editor.commit()
                } else {
                    Log.d("LoadingActivity", "current_orders : ${p0.value}")
                    editor.putString("pendingOrder", false.toString())
                    editor.commit()
                }
            }

        })

        if (emailHash == "") {
            Log.d("LoadingActivity", "Starting LoginActivity")
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }

        if (emailHash != "") {
            Log.d("LoadingActivity", "Fetching data from database")
            val featureRef = FirebaseDatabase.getInstance().getReference("/featured")
            featureRef.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    Log.d("LoadingActivity","Failed to retrieve feature list")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    Log.d("LoadingActivity", "Successfully Fetched featured data")

                    val featureList = mutableListOf<String>()
                    for (name in p0.children) {
                        Log.d("LoadingActivity", "Image Location : ${name.value.toString()}")
                        featureList.add(name.value.toString())
                    }
                    Log.d("LoadingActivity","FeatureList Created")


                    val totalRef = FirebaseDatabase.getInstance().getReference("/total_items")
                    totalRef.addValueEventListener(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                            Log.d("LoadingActivity","Failed to Retrieve total items")
                        }
                        override fun onDataChange(p0: DataSnapshot) {

                            Log.d("LoadingActivity","Successfully to Retrieve total items")
                            val totalHashMap = HashMap<String,String>()
                            for( name in p0.children){
                                totalHashMap.put(name.key.toString(), name.value.toString())
                            }

                            Log.d("LoadingActivity","Creating dataObj")
                            val dataObj =
                                AllData(
                                    totalHashMap,
                                    featureList
                                )
                            val indexIntent = Intent(this@LoadingActivity, IndexActivity::class.java)
                            indexIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            indexIntent.putExtra("dataObj", dataObj)
                            Log.d("LoadingActivity", "User Logged In : Starting IndexActivity")
                            startActivity(indexIntent)
                            finish()
                        }
                    })
                }
            })


        }
    }

    override fun onRestart() {
        val isConnected = HelperUtils.returnConnectionResult(this)
        if(!isConnected){
            Log.d("LoadingActivity","$isConnected")
            finish()
        }
        super.onRestart()
    }
}


