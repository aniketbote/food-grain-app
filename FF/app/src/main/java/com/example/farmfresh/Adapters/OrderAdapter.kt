package com.example.farmfresh.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.farmfresh.Activities.OrderDetailActivity
import com.example.farmfresh.Model.Order
import com.example.farmfresh.R

class OrderAdapter(private val context: Context,
                     val orderList:MutableList<Order>) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }


    class ViewHolder(item: View): RecyclerView.ViewHolder(item){
        val orderButton: Button = item.findViewById(R.id.previousOrder_button)
        val orderId:TextView = item.findViewById(R.id.order_number_prev)
        val dateOfCompletion:TextView = item.findViewById(R.id.order_status_prev)
//        val dateOfCreation:TextView = item.findViewById(R.id.order_date_creation)
//        val total:TextView = item.findViewById(R.id.order_total_prev)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderitem = orderList[position]
        Log.d("OrderDetailsAdapterList","$orderList")
        holder.orderId.text = orderitem.orderId
        holder.dateOfCompletion.text = orderitem.orderCompletionDate
//        holder.dateOfCreation = orderitem.orderCreatedDate
//        holder.total = orderitem.total
        holder.orderButton.setOnClickListener {
            Log.d("OrderDetailsAdapter","${orderitem}")
            val orderDetailsIntent = Intent(context, OrderDetailActivity::class.java)
            orderDetailsIntent.putExtra("orderObj", orderitem)
            context.startActivity(orderDetailsIntent)
        }

    }

}
