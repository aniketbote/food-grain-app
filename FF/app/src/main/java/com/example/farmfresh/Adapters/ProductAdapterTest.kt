package com.example.farmfresh.Adapters

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
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

class ProductAdapterTest(val context:Context,
                     val productList: List<Product>,
                     val cartList:MutableList<CartItem>) :
    RecyclerView.Adapter<ProductAdapterTest.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.img.context
        val db = CartDatabase(context)
        val product: Product = productList[holder.adapterPosition]
        holder.name.text = product.name
        holder.price.text = product.price
        holder.size.text = product.size

        // This checks if the item is present in the cartlist. If it is present then make "add to cart" button INVISIBLE and "Counter" button VISIBLE
        for(i in 0 until cartList.size){
            if(product.name == cartList[i].name){
                if(cartList[i].available.toInt() < cartList[i].count.toInt()){
                    holder.count.number = cartList[i].available
                    db.updateData(cartList[i].name, cartList[i].available)
                    Toast.makeText(context, "Not Enough Quantity Available",Toast.LENGTH_SHORT).show()
                }
                else {
                    holder.count.number = cartList[i].count    // Updating the Counter button text to no of items Previously added
                }
                holder.addToCart.visibility = View.INVISIBLE   // Making add to cart INVISBLE
                holder.count.visibility = View.VISIBLE         // Making Counter VISIBLE
            }
        }


        // This Checks if the Available quantity is equal to zero. If it is equal to zero make the image grey,
        // Make the "add to cart button INVISIBLE", Make ""Counter" button INVISIBLE, Make a textview VISIBLE
        Log.d("Test1","AvailableQuantity for ${product.name} is ${product.availableQuantity}")
        if(product.availableQuantity == 0.toString()){
            Log.d("Test1","AvailableQuantity is zero")
            val colorMatrix:ColorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0.toFloat())
            val filter = ColorMatrixColorFilter(colorMatrix)
            holder.img.colorFilter = filter               // make the image grey
            holder.addToCart.visibility = View.INVISIBLE  // Make the "add to cart button INVISIBLE"
            holder.count.visibility = View.INVISIBLE      // Make "Counter" button INVISIBLE
            holder.unavailable.visibility = View.VISIBLE  // Make a textview VISIBLE
        }
        holder.img.loadImage(product.imageUrl)            // Used to load the image on Imageview


        // This implements the logic which is executed after addtocart is pressed.
        // It basically inserts the items into local database
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

        // This is the counter button. This elegant number button which updates the local db with count of items.
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
                    Toast.makeText(context,"Not Enough Quantity Available",Toast.LENGTH_SHORT).show()
                    holder.count.number = oldValue.toString()
                }
                else {
                    db.updateData(product.name, newValue.toString())
                }
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
        val unavailable:TextView = item.findViewById(R.id.product_unavailable)

    }
}
