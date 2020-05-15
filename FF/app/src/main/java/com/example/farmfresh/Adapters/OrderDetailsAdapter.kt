package com.example.farmfresh.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.farmfresh.Model.OrderItem
import com.example.farmfresh.R
import com.example.farmfresh.Utilities.loadImage

//class OrderDetailsAdapter (private val  context, val orderItemList:List<Order


class OrderDetailsAdapter(private val context: Context,
                          val orderItemList:List<OrderItem>) :
    RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderItemList.size
    }


    class ViewHolder(item: View): RecyclerView.ViewHolder(item){
        val name:TextView = item.findViewById(R.id.product_name_order)
        val count:TextView = item.findViewById(R.id.product_count_order)
        val amount:TextView = item.findViewById(R.id.product_amount_cart)
        val image:ImageView = item.findViewById(R.id.product_image_order)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderItem = orderItemList[position]
        holder.name.text = orderItem.name
        holder.count.text = orderItem.count
        holder.amount.text = orderItem.amount
        holder.image.loadImage(orderItem.imageUrl)

    }


}