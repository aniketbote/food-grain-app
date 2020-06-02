package com.example.farmfresh.Activities

import android.app.ActionBar
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.farmfresh.Adapters.CartAdapter
import com.example.farmfresh.Database.CartDatabase
import com.example.farmfresh.Model.OrderList
import com.example.farmfresh.Model.PlaceOrderResponse
import com.example.farmfresh.R
import com.example.farmfresh.Retrofit.RetrofitClient
import com.example.farmfresh.Utilities.HelperUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_placeorder.*
import kotlinx.android.synthetic.main.activity_toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
lateinit var cartTotalPlaceorder: TextView
lateinit var orderTotalPlaceorder: TextView
lateinit var deliverChargePlaceorder: TextView
class PlaceOrderActivity:AppCompatActivity() {
    var emailHash: String = ""

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 137 && resultCode == Activity.RESULT_OK && data != null){
            val pref = this@PlaceOrderActivity.getSharedPreferences("$emailHash", Context.MODE_PRIVATE)
            Log.d("PlaceOrderActivity","Address received Successsfully")
            val addressHolder = data.getSerializableExtra("address")
            val address_reg: TextView= findViewById(R.id.previous_address)
            address_reg.text = addressHolder.toString()
            val editor = pref.edit()
            editor.putString("previousOrderAddress",addressHolder.toString())
            editor.commit()
            Log.d("PlaceOrderActivity","${addressHolder}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placeorder)
        HelperUtils.checkConnection(this)

        orderTotalPlaceorder = findViewById(R.id.ordertotal_placeorder)
        cartTotalPlaceorder = findViewById(R.id.total_placeorder)
        deliverChargePlaceorder = findViewById(R.id.delivery_placeorder)

        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        emailHash = token.getString("EMAILHASH", "").toString()
        val address = token.getString("address","")
        val tokenPrivate = getSharedPreferences("$emailHash", Context.MODE_PRIVATE)
        val pendingOrder = tokenPrivate.getString("pendingOrder", "")
        val previousOrderAddress = tokenPrivate.getString("previousOrderAddress", "")
        Log.d("CartActivity","$pendingOrder")

        val db = CartDatabase(this)
        val cartList = db.readData()
        if (cartList.size > 0) {
            itemText.visibility = View.VISIBLE
            itemText.text = cartList.size.toString()
            cartCount = cartList.size
        }
        val orderTotal = HelperUtils.getCost(cartList)

        val deliveryCharge = if(orderTotal*0.05 > 50){
            (orderTotal*0.05).toInt()
        } else{
            50
        }
        val cartTotal = orderTotal + deliveryCharge

        if(previousOrderAddress != ""){
            previous_address.text = previousOrderAddress
        }
        else{
            previous_address.text = address
        }
        deliverChargePlaceorder.text = deliveryCharge.toString()
        cartTotalPlaceorder.text = cartTotal.toString()
        orderTotalPlaceorder.text = orderTotal.toString()


        setSupportActionBar(toolbar)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        getSupportActionBar()?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)


        val recycleView: RecyclerView = findViewById(R.id.order_list)
        recycleView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?
        val adapter = CartAdapter(this, cartList)
        recycleView.adapter = adapter

        select_address.setOnClickListener{
            HelperUtils.checkConnection(this)
            Log.d("PlaceOrderActivity","Clicked address")
            val addressIntent = Intent(this,
                AddAutoActivity::class.java)
            startActivityForResult(addressIntent, 137)
        }



        place_order.setOnClickListener {
            HelperUtils.checkConnection(this)
            if(cartList.size == 0){
                Toast.makeText(this,"Nothing in the basket",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(pendingOrder == true.toString()){
                Toast.makeText(this,"You have a pending order",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val paymentIntent = Intent(this, PaymentActivity::class.java)
            paymentIntent.putExtra("address","${previous_address.text}")
            paymentIntent.putExtra("price","${cartTotalPlaceorder.text}")
            paymentIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(paymentIntent)
            finish()
            }
        }

    override fun onOptionsItemSelected(item:MenuItem) : Boolean{
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
           startActivity(indexActivityGlobal)
        }

        return super.onOptionsItemSelected(item);
    }

    override fun onBackPressed() {
        indexActivityGlobal.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        indexActivityGlobal.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(indexActivityGlobal)
    }

}
