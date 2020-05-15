package com.example.farmfresh

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import java.util.stream.IntStream.range

class OrderAdapter(private val context: Context,
                     val order:MutableList<OrderItem>,
                     val type:String) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_previous, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return order.size
    }


    class ViewHolder(item: View): RecyclerView.ViewHolder(item){
        val number:TextView = item.findViewById(R.id.order_number_prev)
        val status:TextView = item.findViewById(R.id.order_status_prev)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderitem = order[position]
       // holder.number.text = orderitem.number
       // holder.date.text = orderitem.date
       // holder.status.text = orderitem.status

    }

}
