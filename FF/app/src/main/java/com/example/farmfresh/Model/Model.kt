package com.example.farmfresh.Model

import java.io.Serializable

data class PlaceOrderResponse(val message:String, val deficiency:String, val errorCode:Int)

data class SubData(val itemList:List<Product>, val totalCount:Int): Serializable

data class AllData(val popularHashMap: HashMap<String, MutableList<Product>>, val totalHashMap: HashMap<String, String>, val featureList: List<String>):
    Serializable

data class Product(val name:String, val description: String, val imageUrl:String, val size: String, val price:String, val availableQuantity:String, val type:String = ""):
    Serializable

data class CartItem(val name:String, val imageUrl:String, val size: String, val price:String, val count:String, val type:String, val available:String)

data class OrderItem(val name: String, val amount:String, val count:String, val imageUrl: String): Serializable

data class Order(val orderId:String, val orderCreatedDate:String, val orderCompletionDate:String, val orderItems:List<OrderItem>, val total: String): Serializable

data class OrderList(val orderList: List<Order>): Serializable

data class ProductList(val itemList:List<Product>): Serializable

data class BraintreeResponse(val success:String, val transaction_id: String): Serializable


