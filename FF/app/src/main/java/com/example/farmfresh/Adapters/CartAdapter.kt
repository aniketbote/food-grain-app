package com.example.farmfresh.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.farmfresh.Activities.cartCount
import com.example.farmfresh.Activities.cartTotal
import com.example.farmfresh.Activities.itemText
import com.example.farmfresh.Database.CartDatabase
import com.example.farmfresh.Model.CartItem
import com.example.farmfresh.R
import com.example.farmfresh.Utilities.HelperUtils.getPosition
import com.example.farmfresh.Utilities.loadImage


class CartAdapter(private val context: Context, private val data:MutableList<CartItem>) : RecyclerView.Adapter<CartAdapter.ViewHolder>()
{
    class ViewHolder(rowView: View):RecyclerView.ViewHolder(rowView) {
        val name: TextView = rowView.findViewById(R.id.product_name_cart)
        val cost: TextView = rowView.findViewById(R.id.product_amount_cart)
        val count: ElegantNumberButton = rowView.findViewById(R.id.product_count_cart)
        val image:ImageView = rowView.findViewById(R.id.product_image_cart)
//        val size: TextView = rowView.findViewById(R.id.product_size_cart)


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
        val db = CartDatabase(context)
        val item: CartItem = data[position]
        holder.name.text  = item.name
        holder.cost.text = (item.price.toInt()*item.count.toInt()).toString()
        if(item.available.toInt() < item.count.toInt()) {
            holder.count.number = item.available
            db.updateData(item.name,item.available)
            Toast.makeText(context,"Not Enough Available Quantity", Toast.LENGTH_SHORT).show()
        }
        else {
            holder.count.number = item.count
        }
        holder.image.loadImage(item.imageUrl)
//        holder.size.text = item.size

        holder.count.setOnValueChangeListener { view, oldValue, newValue ->
            Log.d("Product","Number for ${item.name} is $newValue")
            val context = view.context
            if(newValue == 0){
                //delete
                db.deleteData(item.name)
                cartCount -= 1
                if(cartCount <= 0){
                    itemText.visibility = View.INVISIBLE
                }
                else {
                    itemText.visibility = View.VISIBLE
                    itemText.text = cartCount.toString()
                }
                cartTotal.text = (cartTotal.text.toString().toInt() - item.price.toInt()).toString()
                val remPosition = getPosition(data, item.name)
//                Log.d("CartActivity","Before : ${remPosition}")
//                Log.d("CartActivity","Before : ${data[remPosition]}")
//                Log.d("CartActivity","Before : ${data}")
//                Log.d("CartActivity","Before : ${item.name}")
                data.removeAt(remPosition)
                notifyItemRemoved(remPosition)
//                Log.d("CartActivity","After : ${remPosition}")
//                Log.d("CartActivity","After : ${data[remPosition]}")
//                Log.d("CartActivity","After : ${data}")
            }
            if(newValue >= 1){
                //update
                if((item.available.toInt() - newValue) < 0){
                    Toast.makeText(context,"Not Enough Available",Toast.LENGTH_SHORT).show()
                    holder.count.number = oldValue.toString()
                }
                else {
                    holder.cost.text = (item.price.toInt() * newValue).toString()
                    cartTotal.text = (cartTotal.text.toString().toInt() + (newValue - oldValue) * item.price.toInt()).toString()
                    db.updateData(item.name, newValue.toString())
                }
            }
        }



    }

}