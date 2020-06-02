package com.example.farmfresh.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.farmfresh.Adapters.ProductAdapter
import com.example.farmfresh.Adapters.ProductAdapterTest
import com.example.farmfresh.Database.CartDatabase
import com.example.farmfresh.Model.CartItem
import com.example.farmfresh.Model.Product
import com.example.farmfresh.Model.SubData
import com.example.farmfresh.R
import com.example.farmfresh.Utilities.HelperUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_itemlist.*

//var cartCount_:Int = 0
class ProductActivity : AppCompatActivity(){
    lateinit var cartList:MutableList<CartItem>
    lateinit var productList: MutableList<Product>
    lateinit var adapter: ProductAdapterTest
    var notLoading = true
    lateinit var layoutManager: LinearLayoutManager
    lateinit var TYPE: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itemlist)
        HelperUtils.checkConnection(this)

        val db = CartDatabase(this)
        cartList= db.readData()

        val subDataObj = intent.getSerializableExtra("subDataObj") as SubData
        productList = subDataObj.itemList as MutableList<Product>
        TYPE = productList[0].type
        Log.d("ProductActivity","${productList[productList.size - 1]}")

        val recyclerView:RecyclerView = findViewById(R.id.recycleview)
        layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        adapter= ProductAdapterTest(this, productList, cartList)
        recyclerView.adapter = adapter

        addscrolllistener()

    }

    private fun addscrolllistener() {
        recycleview.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(notLoading && layoutManager.findLastCompletelyVisibleItemPosition() == productList.size -1){
                    progressBar_product.visibility = View.VISIBLE
                    val lastItem = productList[productList.size - 1]
                    notLoading = false

                    val Ref = FirebaseDatabase.getInstance().getReference("/all_items/${TYPE}")
                    Ref
                        .orderByKey()
                        .startAt(lastItem.name)
                        .limitToFirst(5)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                Log.d("ProductActivity", "Error Fetching Fruits Values")
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val itemList = HelperUtils.getAllItemsList(p0)
                                itemList.removeAt(0)
                                if(!itemList.isEmpty()){
                                    productList.addAll(itemList)
                                    adapter.notifyDataSetChanged()
                                    notLoading = true
                                    Log.d("ProductActivity", "${itemList}")
                                    for(p in productList){
                                        Log.d("Test", "${p.name} = ${p.availableQuantity}")
                                    }
                                }
                                else{
                                    Toast.makeText(this@ProductActivity,"Reached End", Toast.LENGTH_SHORT).show()
                                }
                                progressBar_product.visibility = View.GONE

                            }
                        })

                }
            }
        })
    }

}