package com.example.farmfresh

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(val productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var product:Product = productList[position]
        holder.name.text = product.name
        holder.price.text = product.price
        holder.img.loadImage(product.imageUrl)
    }

    class ViewHolder(item: View): RecyclerView.ViewHolder(item){
        var name:TextView = item.findViewById(R.id.product_name)
        var price:TextView = item.findViewById(R.id.product_price)
        var img:ImageView = item.findViewById(R.id.product_image)
    }
}
