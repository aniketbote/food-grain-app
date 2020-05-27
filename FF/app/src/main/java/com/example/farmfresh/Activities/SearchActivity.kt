package com.example.farmfresh.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.farmfresh.Adapters.ProductAdapter
import com.example.farmfresh.Adapters.SearchProductAdapter
import com.example.farmfresh.Database.CartDatabase
import com.example.farmfresh.Model.CartItem
import com.example.farmfresh.Model.ProductList
import com.example.farmfresh.Model.SubData
import com.example.farmfresh.R
import com.example.farmfresh.Retrofit.RetrofitClient
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_index.*
import kotlinx.android.synthetic.main.activity_toolbar.*
import kotlinx.android.synthetic.main.toolbar_searchbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity: AppCompatActivity() {
    lateinit var cartList: MutableList<CartItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val db = CartDatabase(this)
        cartList = db.readData()






        setSupportActionBar(toolbar_searchbar)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_search, menu)
        val searchView: SearchView =
            menu?.findItem(R.id.menu_search)?.actionView as SearchView
//        searchView.setIconifiedByDefault(true)
//        searchView.focusable = View.FOCUSABLE
//        searchView.isIconified = false
//        searchView.requestFocusFromTouch()
        searchView.onActionViewExpanded()
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("IndexActivity", "${query}")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                Log.d("IndexActivity", "${newText}")
                if (newText != null) {

                    RetrofitClient.instance.search(newText)
                        .enqueue(object : Callback<ProductList> {
                            override fun onFailure(call: Call<ProductList>, t: Throwable) {
                                Log.d("IndexActivity", "${t.message}")
                            }

                            override fun onResponse(
                                call: Call<ProductList>,
                                response: Response<ProductList>
                            ) {
                                var adapter = SearchProductAdapter(
                                    response.body()?.itemList!!,
                                    cartList
                                )
                                val recyclerView: RecyclerView = findViewById(R.id.recycleview_search)
                                recyclerView.layoutManager = LinearLayoutManager(this@SearchActivity, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?
                                recyclerView.adapter = adapter
                                Log.d("IndexActivity", "${response.body()?.itemList}")

                            }

                        })
                }
                return true
            }

        })
            return true
    }
}


