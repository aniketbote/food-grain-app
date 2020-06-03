package com.example.farmfresh.Activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.farmfresh.Adapters.OrderAdapter
import com.example.farmfresh.Model.Order
import com.example.farmfresh.Model.OrderList
import com.example.farmfresh.R
import com.example.farmfresh.Utilities.HelperUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_previous.*

class PreviousOrdersActivity : AppCompatActivity(){
    lateinit var adapter:OrderAdapter
    var notLoading = true
    lateinit var layoutManager: LinearLayoutManager
    lateinit var orderList: MutableList<Order>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous)
        HelperUtils.checkConnection(this)

        val orderListObj = intent.getSerializableExtra("orderListObj") as OrderList
        Log.d("PreviousActivity","${orderListObj.orderList}")
        orderList = orderListObj.orderList as MutableList<Order>

        val recycleView: RecyclerView = findViewById(R.id.previous_recycleview)
        layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)
        recycleView.layoutManager = layoutManager
        adapter = OrderAdapter(this, orderList)
        recycleView.adapter = adapter
        addscrolllistener()

    }
    private fun addscrolllistener() {
        previous_recycleview.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(notLoading && layoutManager.findLastCompletelyVisibleItemPosition() == orderList.size -1){
                    progressBar_previous.visibility = View.VISIBLE
                    val lastItem = orderList[orderList.size - 1]
                    notLoading = false

                    val currentRef = FirebaseDatabase.getInstance().getReference("all_orders/$emailHashGlobal/previous")
                        .orderByChild("OrderTime")
                        .limitToFirst(5)
                        .startAt(lastItem.orderTime.toDouble())
                    currentRef.addValueEventListener(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {
                                Log.d("PreviousActivity", "Error Fetching Fruits Values")
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val nextOrderList = HelperUtils.getOrderList(p0)
                                Log.d("PreviousActivity", "${p0}")
                                nextOrderList.removeAt(0)
                                if(!nextOrderList.isEmpty()){
                                    orderList.addAll(nextOrderList)
                                    adapter.notifyDataSetChanged()
                                    notLoading = true
                                    Log.d("ProductActivity", "${orderList}")
                                }
                                else{
                                    Toast.makeText(this@PreviousOrdersActivity,"Reached End", Toast.LENGTH_SHORT).show()
                                }
                                progressBar_previous.visibility = View.GONE

                            }
                        })
                }
            }
        })
    }

}

