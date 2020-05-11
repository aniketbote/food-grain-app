package com.example.farmfresh

import android.util.Log
import android.util.Patterns
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import java.io.Serializable
import java.security.MessageDigest


fun ImageView.loadImage(uri: String?) {
    val options = RequestOptions()
        .placeholder(R.drawable.home)
        .circleCrop()
        .error(R.mipmap.ic_launcher_round)
    Glide.with(this.context)
        .setDefaultRequestOptions(options)
        .load(uri)
        .into(this)
}

object HelperUtils {
    fun getList(p0: DataSnapshot): MutableList<Product> {
        val finalList = mutableListOf<Product>()
        for (itemName in p0.children) {
            val productObj = Product(
                itemName.key.toString(),
                itemName.child("Description").value.toString(),
                itemName.child("Image").value.toString(),
                itemName.child("Size").value.toString(),
                itemName.child("Price").value.toString(),
                itemName.child("Available Quantity").value.toString())
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
    fun getCatObj(itemList:List<Product>, totalCount:String): SubData {
        val catDataObj = SubData(itemList, totalCount.toInt())
        return catDataObj
    }
}


data class SubData(val itemList:List<Product>, val totalCount:Int): Serializable

data class AllData(val itemList:List<Product>, val totalHashMap: HashMap<String, String>, val featureList: List<String>): Serializable

data class Product(val name:String, val description: String, val imageUrl:String, val size: String, val price:String, val availableQuantity:String): Serializable

data class CartItem(val name:String, val imageUrl:String, val size: String, val price:String, val count:String, val type:String)

