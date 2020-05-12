package com.example.farmfresh

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_current.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrentOrdersActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current)

        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        val emailHash = token.getString("EMAILHASH", "")
        val pendingOrder = token.getString("pendingOrder", "")

        if(pendingOrder == false.toString()){
            receivedOrder_current.visibility = View.GONE
        }
        if(pendingOrder == true.toString()){
            receivedOrder_current.visibility = View.VISIBLE
        }

        receivedOrder_current.setOnClickListener {
            RetrofitClient.instance.orderreceived(emailHash.toString())
                .enqueue(object : Callback<PlaceOrderResponse>{
                    override fun onFailure(call: Call<PlaceOrderResponse>, t: Throwable) {
                        Log.d("CurrentActivity","Failed : ${t.message}")
                    }

                    override fun onResponse(
                        call: Call<PlaceOrderResponse>,
                        response: Response<PlaceOrderResponse>
                    ) {
                        Toast.makeText(this@CurrentOrdersActivity, "${response.body()?.message}", Toast.LENGTH_SHORT).show()
                        Log.d("CurrentActivity", "Successfull : ${response.body()?.message}")
                        if(response.body()?.errorCode == 0) {
                            val pref = this@CurrentOrdersActivity.getSharedPreferences(
                                "UserSharedPreferences",
                                Context.MODE_PRIVATE
                            )
                            val editor = pref.edit()
                            editor.putString("pendingOrder", false.toString())
                            editor.commit()
                            startActivity(intent)
                            finish()
                        }
                    }

                })
        }
    }
}