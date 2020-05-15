package com.example.farmfresh.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.farmfresh.Adapters.OrderAdapter
import com.example.farmfresh.Adapters.OrderDetailsAdapter
import com.example.farmfresh.Model.Order
import com.example.farmfresh.R
import kotlinx.android.synthetic.main.activity_order_details.*

class OrderDetailActivity :AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)
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