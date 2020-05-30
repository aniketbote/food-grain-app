package com.example.farmfresh.Activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.farmfresh.*
import com.example.farmfresh.Adapters.OrderAdapter
import com.example.farmfresh.Model.Order
import com.example.farmfresh.Model.OrderList
import com.example.farmfresh.Model.PlaceOrderResponse
import com.example.farmfresh.Retrofit.RetrofitClient
import kotlinx.android.synthetic.main.activity_current.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrentOrdersActivity : AppCompatActivity(){
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
        setContentView(R.layout.activity_current)
        checkConnection(this)
        val orderListObj = intent.getSerializableExtra("orderListObj") as OrderList

        if(orderListObj.orderList.isNotEmpty()){
            Log.d("CurrentActivity", "${orderListObj.orderList.isNotEmpty()}")
            receivedOrder_current.visibility = View.VISIBLE
        }
        else{
            receivedOrder_current.visibility = View.INVISIBLE
        }

        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        val emailHash = token.getString("EMAILHASH", "")

//        Log.d("CurrentActivity", "$pendingOrder")
//        if(pendingOrder == false.toString()){
//            receivedOrder_current.visibility = View.INVISIBLE
//        }
//        if(pendingOrder == true.toString()){
//            receivedOrder_current.visibility = View.VISIBLE
//        }


        Log.d("CurrentActivity","${orderListObj.orderList}")
        val recycleView: RecyclerView = findViewById(R.id.current_recycler)
        recycleView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false) as RecyclerView.LayoutManager?
        val adapter = OrderAdapter(
            this,
            orderListObj.orderList as MutableList<Order>
        )
        recycleView.adapter = adapter






        receivedOrder_current.setOnClickListener {
            if(orderListObj.orderList.isEmpty()){
                Toast.makeText(this@CurrentOrdersActivity, "No Current orders", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //progress bar
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.progress_bar,null)
            builder.setView(dialogView)
            builder.setCancelable(false)
            val message = dialogView.findViewById<TextView>(R.id.text_progressBar)
            message.text = "Please Wait while we make changes"
            val dialog = builder.create()
            dialog.show()

            RetrofitClient.instance.orderreceived(emailHash.toString())
                .enqueue(object : Callback<PlaceOrderResponse>{
                    override fun onFailure(call: Call<PlaceOrderResponse>, t: Throwable) {
                        dialog.dismiss()
                        Toast.makeText(this@CurrentOrdersActivity,"Failed : ${t.localizedMessage}",Toast.LENGTH_SHORT).show()
                        Log.d("CurrentActivity","Failed : ${t.message}")
                        return
                    }

                    override fun onResponse(
                        call: Call<PlaceOrderResponse>,
                        response: Response<PlaceOrderResponse>
                    ) {
                        Log.d("CurrentActivity", "Successfull : ${response.body()?.message}")
                        if(response.body() == null){
                            dialog.dismiss()
                            Toast.makeText(this@CurrentOrdersActivity, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                        }
                        if(response.body()?.errorCode == 0) {
                            val pref = getSharedPreferences(
                                "${emailHash}",
                                Context.MODE_PRIVATE
                            )
                            val editor = pref.edit()
                            editor.putString("pendingOrder", false.toString())
                            editor.commit()
                            Toast.makeText(this@CurrentOrdersActivity, "${response.body()?.message}", Toast.LENGTH_SHORT).show()
                            val orderListObj =
                                OrderList(
                                    mutableListOf()
                                )
                            val currentOrdersIntent = Intent(this@CurrentOrdersActivity, CurrentOrdersActivity::class.java)
                            currentOrdersIntent.putExtra("orderListObj",orderListObj)
                            currentOrdersIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                            dialog.dismiss()
                            startActivity(currentOrdersIntent)
                            finish()
                        }
                        if(response.body()?.errorCode == 2) {
                            dialog.dismiss()
                            Toast.makeText(this@CurrentOrdersActivity, "${response.body()?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                })
        }

    }
}