package com.example.farmfresh.Activities

import android.content.Context
import android.content.Intent
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
import com.example.farmfresh.Adapters.CartAdapter
import com.example.farmfresh.Database.CartDatabase
import com.example.farmfresh.Model.OrderList
import com.example.farmfresh.Model.PlaceOrderResponse
import com.example.farmfresh.Retrofit.RetrofitClient
import com.example.farmfresh.Utilities.HelperUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_cart.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

lateinit var cartTotal: TextView

class CartActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartTotal = findViewById(
            R.id.cart_amount
        )

        val db = CartDatabase(this)
        val cartList = db.readData()
        if(cartList.size > 0){
            itemText.visibility = View.VISIBLE
            itemText.text = cartList.size.toString()
            cartCount = cartList.size
        }

        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        val emailHash = token.getString("EMAILHASH", "")
        val pendingOrder = token.getString("pendingOrder", "")
        Log.d("CartActivity","$pendingOrder")
        Log.d("CartActivity","$cartList")

        cartTotal.text = HelperUtils.getCost(
            cartList
        ).toString()

        val recycleView: RecyclerView = findViewById(R.id.cart_list)
        recycleView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false) as RecyclerView.LayoutManager?
        val adapter = CartAdapter(this, cartList)
        recycleView.adapter = adapter






        placeOrder_cart.setOnClickListener {
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
            Log.d("CartActivity","$cartJsonObj")
            Log.d("CartActivity","Pressed Place Order Button")
            RetrofitClient.instance.placeorder(cartJsonObj, emailHash.toString())
                .enqueue(object : Callback<PlaceOrderResponse>{
                    override fun onFailure(call: Call<PlaceOrderResponse>, t: Throwable) {
                        dialog.dismiss()
                        Toast.makeText(this@CartActivity,"Failed : ${t.localizedMessage}",Toast.LENGTH_SHORT).show()
                        Log.d("CartActivity","Failed : ${t.message}")
                    }

                    override fun onResponse(call: Call<PlaceOrderResponse>, response: Response<PlaceOrderResponse>) {
                        Log.d("CartActivity","Successfull : ${response.body()?.message}")
                        if(response.body() == null){
                            dialog.dismiss()
                            Toast.makeText(this@CartActivity, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                        }
                        if(response.body()?.errorCode == 0){
                            Log.d("CartActivity","Setting shared preferences")
                            val pref = this@CartActivity.getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
                            val editor = pref.edit()
                            editor.putString("pendingOrder", true.toString())
                            editor.commit()
                            for(i in 0 until cartList.size){
                                db.deleteData(cartList[i].name)
                            }
                            cartCount = 0
                            itemText.visibility = View.INVISIBLE

                            val currentRef = FirebaseDatabase.getInstance().getReference("all_orders/$emailHashGlobal/current")
                            currentRef.addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                    Log.d("CartActivity","Error occured: ${p0}")
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
                                    Log.d("CartActivity","${orderList}")
                                    val currentOrdersIntent = Intent(this@CartActivity, CurrentOrdersActivity::class.java)
                                    currentOrdersIntent.putExtra("orderListObj",orderListObj)
                                    currentOrdersIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    Toast.makeText(this@CartActivity,"${response.body()?.message}",Toast.LENGTH_SHORT).show()
                                    startActivity(currentOrdersIntent)
                                    dialog.dismiss()
                                    finish()
                                }
                            })
                        }

                        if(response.body()?.errorCode == 1){
                            val deficientItems = response.body()!!.deficiency.split(',')
                            Log.d("CartActivity","${deficientItems}")
                            for( i in 0 until deficientItems.size-1){
                                db.deleteData(deficientItems[i])
                            }
                            dialog.dismiss()
                            Toast.makeText(this@CartActivity,"${response.body()?.message}",Toast.LENGTH_SHORT).show()
                            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                            startActivity(intent)
                            finish()
                        }
                        if(response.body()?.errorCode == 2){
                            Log.d("CartActivity","Error code 2")
                            dialog.dismiss()
                            Toast.makeText(this@CartActivity,"${response.body()?.message}",Toast.LENGTH_SHORT).show()
                            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                            startActivity(intent)
                            finish()
                        }


                    }

                })

        }

    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Log.d("Back","CartActivity")
            val indexIntent =
                indexActivityGlobal
            indexIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            indexIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(indexIntent)
        }
        return super.onKeyDown(keyCode, event)
    }
}