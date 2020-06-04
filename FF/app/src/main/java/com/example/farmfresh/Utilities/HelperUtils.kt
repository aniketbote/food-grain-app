package com.example.farmfresh.Utilities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.util.Patterns
import android.widget.ImageView
import androidx.core.app.ActivityCompat.startActivityForResult
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.farmfresh.Activities.NoConnectionActivity
import com.example.farmfresh.Model.*
import com.example.farmfresh.R
import com.google.firebase.database.DataSnapshot
import java.security.MessageDigest


fun ImageView.loadImage(uri: String?) {
    val options = RequestOptions()
        .placeholder(R.drawable.home)
        .circleCrop()
        .error(R.mipmap.ic_launcher)
    Glide.with(this.context)
        .applyDefaultRequestOptions(options)
        .load(uri)
        .into(this)
}

object HelperUtils {
    fun getOrderList(p0: DataSnapshot): MutableList<Order> {
        val finalList = mutableListOf<Order>()
        for(order in p0.children){
            val orderItemList = mutableListOf<OrderItem>()
            val orderId = order.key.toString()
            val dateofCompletion = order.child("Date of Completion").value.toString()
            val dateofCreation = order.child("Date of Order").value.toString()
            val total = order.child("Total").value.toString()
            val ordertime = order.child("OrderTime").value.toString().toInt()
            for (item in order.child("Items").children){
                val orderitemObj = OrderItem(
                    item.key.toString(),
                    item.child("Amount").value.toString(),
                    item.child("Count").value.toString(),
                    item.child("Image").value.toString()
                )
                orderItemList.add(orderitemObj)
            }
            val orderObj = Order(
                orderId,
                dateofCreation,
                dateofCompletion,
                orderItemList,
                total,
                ordertime
            )
            finalList.add(orderObj)
        }
        return finalList
    }
    fun getAllItemsList(p0: DataSnapshot): MutableList<Product> {
        val finalList = mutableListOf<Product>()
        for (itemName in p0.children) {
            val productObj = Product(
                itemName.key.toString(),
                itemName.child("Description").value.toString(),
                itemName.child("Image").value.toString(),
                itemName.child("Size").value.toString(),
                itemName.child("Price").value.toString(),
                itemName.child("Available Quantity").value.toString(),
                itemName.child("Type").value.toString()
            )
            finalList.add(productObj)
        }
        return finalList
    }

    fun generatehash(stringToBeHashed:String): String {
        val bytes = stringToBeHashed.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

    fun EmailValidate(email: String): Boolean {
        Log.d("RegisterActivity","Funtion : email validate")
        return Patterns.EMAIL_ADDRESS.toRegex().matches(email)
    }
    fun getCatObj(itemList:List<Product>): ProductList {
        val catDataObj = ProductList(itemList)
        return catDataObj
    }

    fun getCost(data: MutableList<CartItem>): Int {
        var total = 0
        for(i in 0 until data.size){
            total += (data[i].price.toInt() * data[i].count.toInt())
        }
        return total
    }
    fun getPosition(data:MutableList<CartItem>, name: String): Int {
        var pos = -1
        for(i in 0 until data.size){
            if (data[i].name == name){
                pos = i
                break
            }
        }
        return pos
    }
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.d("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.d("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.d("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    fun checkConnection(context: Context) {
        val isConnected = isOnline(context)
        Log.d("LoadingActivity", "$isConnected")

        if(!isConnected){
            Log.d("LoadingActivity", "No connection : Starting No Connection Activity")
            val noConnectionIntent = Intent(context, NoConnectionActivity::class.java)
            context.startActivity(noConnectionIntent)
        }
    }
    fun returnConnectionResult(context: Context): Boolean {
        val isConnected = isOnline(context)
        return isConnected

    }

}



