package com.example.farmfresh.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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


        val recycleView: RecyclerView = findViewById(R.id.order_list)
        recycleView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?
        val adapter = CartAdapter(this, cartList)
        recycleView.adapter = adapter

        select_address.setOnClickListener{
            Log.d("PlaceOrderActivity","Clicked address")
            val addressIntent = Intent(this,
                AddAutoActivity::class.java)
            startActivityForResult(addressIntent, 137)
        }



        place_order.setOnClickListener {
            if(cartList.size == 0){
                Toast.makeText(this,"Nothing in the basket",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(pendingOrder == true.toString()){
                Toast.makeText(this,"You have a pending order",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //progress bar
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.progress_bar,null)
            builder.setView(dialogView)
            builder.setCancelable(false)
            val message = dialogView.findViewById<TextView>(R.id.text_progressBar)
            message.text = "Please Wait while we place your order"
            val dialog = builder.create()
            dialog.show()

            val db = CartDatabase(this)
            val cartJsonObj = db.readDataJson()
            Log.d("PlaceOrderActivity","$cartJsonObj")
            Log.d("PlaceOrderActivity","Pressed Place Order Button")
            val orderAddress = previous_address.text.toString()
            Log.d("PlaceOrderActivity","$orderAddress")
            RetrofitClient.instance.placeorder(cartJsonObj, emailHash.toString(),orderAddress)
                .enqueue(object : Callback<PlaceOrderResponse> {
                    override fun onFailure(call: Call<PlaceOrderResponse>, t: Throwable) {
                        dialog.dismiss()
                        Toast.makeText(this@PlaceOrderActivity,"Failed : ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                        Log.d("PlaceOrderActivity","Failed : ${t.message}")
                    }

                    override fun onResponse(call: Call<PlaceOrderResponse>, response: Response<PlaceOrderResponse>) {
                        Log.d("PlaceOrderActivity","Successful : ${response.body()?.message}")
                        if(response.body() == null){
                            dialog.dismiss()
                            Toast.makeText(this@PlaceOrderActivity, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                        }
                        if(response.body()?.errorCode == 0){
                            Log.d("PlaceOrderActivity","Setting shared preferences")
                            val pref = this@PlaceOrderActivity.getSharedPreferences("$emailHash", Context.MODE_PRIVATE)
                            val editor = pref.edit()
                            editor.putString("pendingOrder", true.toString())
                            editor.putString("previousOrderAddress",orderAddress)
                            editor.commit()
                            for(i in 0 until cartList.size){
                                db.deleteData(cartList[i].name)
                            }
                            cartCount = 0
                            itemText.visibility = View.INVISIBLE

                            val currentRef = FirebaseDatabase.getInstance().getReference("all_orders/$emailHashGlobal/current")
                            currentRef.addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                    Log.d("PlaceOrderActivity","Error occured: ${p0}")
                                    return
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    val orderList =
                                        HelperUtils.getOrderList(
                                            p0
                                        )
                                    val orderListObj =
                                        OrderList(
                                            orderList
                                        )
                                    Log.d("PlaceOrderActivity","${orderList}")
                                    val currentOrdersIntent = Intent(this@PlaceOrderActivity, CurrentOrdersActivity::class.java)
                                    currentOrdersIntent.putExtra("orderListObj",orderListObj)
                                    currentOrdersIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    Toast.makeText(this@PlaceOrderActivity,"${response.body()?.message}",
                                        Toast.LENGTH_SHORT).show()
                                    startActivity(currentOrdersIntent)
                                    dialog.dismiss()
                                    finish()
                                }
                            })
                        }

                        if(response.body()?.errorCode == 1){
                            val deficientItems = response.body()!!.deficiency.split(',')
                            Log.d("PlaceOrderActivity","${deficientItems}")
                            for( i in 0 until deficientItems.size-1){
                                db.deleteData(deficientItems[i])
                            }
                            dialog.dismiss()
                            Toast.makeText(this@PlaceOrderActivity,"${response.body()?.message}", Toast.LENGTH_SHORT).show()
                            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                            startActivity(intent)
                            finish()
                        }
                        if(response.body()?.errorCode == 2){
                            Log.d("PlaceOrderActivity","Error code 2")
                            dialog.dismiss()
                            Toast.makeText(this@PlaceOrderActivity,"${response.body()?.message}", Toast.LENGTH_SHORT).show()
                            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                            startActivity(intent)
                            finish()
                        }

                    }
                })
            }
        }

    }
