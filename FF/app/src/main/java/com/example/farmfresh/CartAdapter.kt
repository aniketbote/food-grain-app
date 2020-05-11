package com.example.farmfresh

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.with
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton


class CartAdapter(private val context: Context, private val data:MutableList<CartItem>) : RecyclerView.Adapter<CartAdapter.ViewHolder>()
{
    class ViewHolder(rowView: View):RecyclerView.ViewHolder(rowView) {

        val name: TextView = rowView.findViewById(R.id.product_name_cart)
        val cost: TextView = rowView.findViewById(R.id.product_amount_cart)
        val count: ElegantNumberButton = rowView.findViewById(R.id.product_count_cart)
        val image:ImageView = rowView.findViewById(R.id.product_image_cart)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.item_cart_view, parent, false)
        return ViewHolder(rowView)
    }

    override fun getItemCount(): Int {
       return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item: CartItem = data[position]
        holder.name.text  = item.name
        holder.cost.text = (item.price.toInt()*item.count.toInt()).toString()
        holder.count.number = item.count
        holder.image.loadImage(item.imageUrl)

    }

}