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
import com.example.farmfresh.Utilities.HelperUtils
import com.example.farmfresh.Utilities.loadImage

class SearchProductAdapter(val context: Context, val productList: List<Product>) :
    RecyclerView.Adapter<SearchProductAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_searchitem, parent, false)
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

        val cartList = db.readData()

        val pos = HelperUtils.getPosition(cartList, product.name)
        val colorMatrix: ColorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0.toFloat())
        val filter = ColorMatrixColorFilter(colorMatrix)

        if (pos == -1 && product.availableQuantity == 0.toString()) {
            Log.d("Test2", "Block1 : ${product.name}")
            holder.img.colorFilter = filter               // make the image grey
            holder.addToCart.visibility = View.INVISIBLE  // Make the "add to cart button INVISIBLE"
            holder.count.visibility = View.INVISIBLE      // Make "Counter" button INVISIBLE
            holder.unavailable.visibility = View.VISIBLE  // Make a textview VISIBLE
        }

        //For item not in cart and available quantity is non-zero
        else if (pos == -1 && product.availableQuantity != 0.toString()) {
            Log.d("Test2", "Block2 : ${product.name}")
            holder.img.colorFilter = null               // make the image grey
            holder.addToCart.visibility = View.VISIBLE  // Make the "add to cart button INVISIBLE"
            holder.count.visibility = View.INVISIBLE      // Make "Counter" button INVISIBLE
            holder.unavailable.visibility = View.INVISIBLE  // Make a textview VISIBLE
        }
        // For item present in cart with non-zero available quantity
        else if (pos != -1 && product.availableQuantity != 0.toString()) {
            Log.d("Test2", "Block3 : ${product.name}")
            // For available quantity < quantity selected in cart
            if (cartList[pos].available.toInt() < cartList[pos].count.toInt()) {
                Log.d("Test2", "Block3-1 : ${product.name}")
                holder.count.number = cartList[pos].available
                db.updateData(cartList[pos].name, cartList[pos].available)
                Toast.makeText(context, "Not Enough Quantity Available", Toast.LENGTH_SHORT).show()
            }
            // For available quantity > quantity selected in cart
            else {
                Log.d("Test2", "Block3-2 : ${product.name}")
                holder.count.number =
                    cartList[pos].count    // Updating the Counter button text to no of items Previously added
            }
            holder.img.colorFilter = null               // make the image grey
            holder.addToCart.visibility = View.INVISIBLE   // Making add to cart INVISBLE
            holder.count.visibility = View.VISIBLE         // Making Counter VISIBLE
            holder.unavailable.visibility = View.INVISIBLE  // Make a textview VISIBLE
        }

        // For item present in cart but 0 available quantity
        else if (pos != -1 && product.availableQuantity == 0.toString()) {
            Log.d("Test2", "Block4 : ${product.name}")
            val resultDel = db.deleteData(product.name)
            Log.d("ProductAdapter", "$resultDel")
            holder.img.colorFilter = filter
            holder.addToCart.visibility = View.INVISIBLE   // Making add to cart INVISBLE
            holder.count.visibility = View.INVISIBLE         // Making Counter VISIBLE
            holder.unavailable.visibility = View.VISIBLE  // Make a textview VISIBLE
            cartCount -= 1
            if(cartCount == 0){
                itemText.visibility = View.INVISIBLE
            }
            else {
                itemText.visibility = View.VISIBLE
                itemText.text = cartCount.toString()
            }
        }
        else{
            Log.d("Test2","Missed Something")
        }

        holder.img.loadImage(product.imageUrl)
        holder.addToCart.setOnClickListener {
            var test:String = "test"
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
        val name:TextView = item.findViewById(R.id.searchproduct_name)
        val price:TextView = item.findViewById(R.id.searchproduct_price)
        val img:ImageView = item.findViewById(R.id.searchproduct_image)
        val size:TextView = item.findViewById(R.id.searchproduct_weight)
        val addToCart:Button = item.findViewById(R.id.searchproduct_addtocart)
        val count:ElegantNumberButton = item.findViewById((R.id.searchproduct_count))
        val unavailable:TextView = item.findViewById(R.id.searchproduct_unavailable)

    }
}
