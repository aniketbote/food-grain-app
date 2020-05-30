package com.example.farmfresh.Activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.farmfresh.Adapters.OrderAdapter
import com.example.farmfresh.Adapters.OrderDetailsAdapter
import com.example.farmfresh.Model.Order
import com.example.farmfresh.R
import kotlinx.android.synthetic.main.activity_order_details.*

class OrderDetailActivity :AppCompatActivity(){
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
        setContentView(R.layout.activity_order_details)
        checkConnection(this)
        val orderObj = intent.getSerializableExtra("orderObj") as Order
        order_number.text = orderObj.orderId
        order_status.text = orderObj.orderCompletionDate
        order_date.text = orderObj.orderCreatedDate

        val recycleView: RecyclerView = findViewById(R.id.orderDetails_recycker)
        recycleView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false) as RecyclerView.LayoutManager?
        val adapter = OrderDetailsAdapter(this, orderObj.orderItems)
        recycleView.adapter = adapter



    }
}