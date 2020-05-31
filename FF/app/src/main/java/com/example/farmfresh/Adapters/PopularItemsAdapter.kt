package com.example.farmfresh.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.farmfresh.Activities.cartCount
import com.example.farmfresh.Activities.itemText
import com.example.farmfresh.Database.CartDatabase
import com.example.farmfresh.Model.CartItem
import com.example.farmfresh.Model.Product
import com.example.farmfresh.R
import com.example.farmfresh.Utilities.loadImage

class PopularItemsAdapter(val productList: List<Product>,
                          val cartList:MutableList<CartItem>): RecyclerView.Adapter<PopularItemsAdapter.ViewHolder>() {

    class ViewHolder(item: View): RecyclerView.ViewHolder(item){
        val name: TextView = item.findViewById(R.id.product_name_index)
        val price: TextView = item.findViewById(R.id.product_price_index)
        val img: ImageView = item.findViewById(R.id.product_img_index)
        val size: TextView = item.findViewById(R.id.product_weight_index)
        val addToCart: Button = item.findViewById(R.id.product_addtocart_index)
        val count: ElegantNumberButton = item.findViewById(R.id.product_count_index)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.popularitem_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.img.context
        val db = CartDatabase(context)
        val product: Product = productList[position]
        holder.name.text = product.name
        holder.price.text = product.price
        holder.size.text = product.size

        for(i in 0 until cartList.size){
            holder.count.number = cartList[i].count
            holder.addToCart.visibility = View.INVISIBLE
            holder.count.visibility = View.VISIBLE
            }


        holder.img.loadImage(product.imageUrl)
        holder.addToCart.setOnClickListener {
            holder.addToCart.visibility = View.GONE
            holder.count.number = 1.toString()
            Log.d("Product","Clicked ${product.name}, count = ${holder.count.number}")
            val cartItemObj = CartItem(
                product.name,
                product.imageUrl,
                product.size,
                product.price,
                holder.count.number,
                product.type,
                product.availableQuantity
            )
            val result = db.insertData(cartItemObj)
            if(result == (-1).toLong()){
                Log.d("ProductAdapter","Error in Inserting values")
                return@setOnClickListener
            }
            holder.count.visibility = View.VISIBLE
            // SOME CODE HERE TO UPDATE THE NUMBER OF ITEMS IN CART
            cartCount += 1
            itemText.visibility = View.VISIBLE
            itemText.text = cartCount.toString()
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
                cartCount -= 1
                if(cartCount == 0){
                    itemText.visibility = View.INVISIBLE
                }
                else {
                    itemText.visibility = View.VISIBLE
                    itemText.text = cartCount.toString()
                }

            }
            if(newValue >= 1){
                //update
                if((product.availableQuantity.toInt() - newValue) < 0){
                    Toast.makeText(context,"Not Enough Quantity Available", Toast.LENGTH_SHORT).show()
                    holder.count.number = oldValue.toString()
                }
                else {
                    db.updateData(product.name, newValue.toString())
                }
            }
        }


    }
}