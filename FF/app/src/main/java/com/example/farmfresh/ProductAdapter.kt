package com.example.farmfresh

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import java.util.stream.IntStream.range

class ProductAdapter(val productList: List<Product>,
                     val cartList:MutableList<CartItem>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product:Product = productList[position]
        holder.name.text = product.name
        holder.price.text = product.price
        holder.size.text = product.size
        holder.img.loadImage(product.imageUrl)

        for(i in 0 until cartList.size){
            if(product.name == cartList[i].name){
                holder.count.number = cartList[i].count
                holder.addToCart.visibility = View.INVISIBLE
                holder.count.visibility = View.VISIBLE
            }
        }


        val context = holder.img.context
        holder.addToCart.setOnClickListener {
            holder.count.number = 1.toString()
            Log.d("Product","Clicked ${product.name}, count = ${holder.count.number}")
            val cartItemObj = CartItem(product.name,product.imageUrl, product.size, product.price, holder.count.number)
            val db = CartDatabase(context)
            val result = db.insertData(cartItemObj)
            if(result == (-1).toLong()){
                Log.d("ProductAdapter","Error in Inserting values")
                return@setOnClickListener
            }
            holder.addToCart.visibility = View.INVISIBLE
            holder.count.visibility = View.VISIBLE
        }


        holder.count.setOnValueChangeListener { view, oldValue, newValue ->
            Log.d("Product","Number for ${product.name} is $newValue")
            val context = view.context
            val db = CartDatabase(context)
            if(newValue == 0){
                //delete
                db.deleteData(product.name)
                holder.addToCart.visibility = View.VISIBLE
                holder.count.visibility = View.INVISIBLE
            }
            if(newValue > 1){
                //update
                db.updateData(product.name, newValue.toString())
            }
        }





    }

    class ViewHolder(item: View): RecyclerView.ViewHolder(item){
        val name:TextView = item.findViewById(R.id.product_name)
        val price:TextView = item.findViewById(R.id.product_price)
        val img:ImageView = item.findViewById(R.id.product_image)
        val size:TextView = item.findViewById(R.id.product_weight)
        val addToCart:Button = item.findViewById(R.id.product_addtocart)
        val count:ElegantNumberButton = item.findViewById((R.id.product_count))

    }
}
