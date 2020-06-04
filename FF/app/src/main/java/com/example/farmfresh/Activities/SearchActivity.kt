package com.example.farmfresh.Activities

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.Menu

import android.view.MenuItem
import android.view.MenuItem.OnActionExpandListener
import android.view.View

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat.setOnActionExpandListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.farmfresh.Adapters.ProductAdapter
import com.example.farmfresh.Adapters.SearchProductAdapter
import com.example.farmfresh.Database.CartDatabase
import com.example.farmfresh.Model.CartItem
import com.example.farmfresh.Model.ProductList
import com.example.farmfresh.R
import com.example.farmfresh.Retrofit.RetrofitClient
import com.example.farmfresh.Utilities.HelperUtils
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_index.*
import kotlinx.android.synthetic.main.activity_toolbar.*
import kotlinx.android.synthetic.main.toolbar_searchbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        HelperUtils.checkConnection(this)


        setSupportActionBar(toolbar_searchbar)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_search, menu)
        val searchItem: MenuItem? = menu?.findItem(R.id.menu_search)
        searchItem?.expandActionView()
        val searchView: androidx.appcompat.widget.SearchView =
            menu?.findItem(R.id.menu_search)?.actionView as SearchView
        searchView.requestFocus()





        searchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                Log.d("SearchActivity", "Clicked Back button")
                val indexIntent = Intent(this@SearchActivity, IndexActivity::class.java)
                indexIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                Log.d("LoadingActivity", "User Logged In : Starting IndexActivity")
                startActivity(indexIntent)
                finish()
                return true
            }
        })


        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("SearchActivity", "${query}")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                Log.d("SearchActivity", "${newText}")
                if (newText != null) {

                    RetrofitClient.instance.search(newText)
                        .enqueue(object : Callback<ProductList> {
                            override fun onFailure(call: Call<ProductList>, t: Throwable) {
                                Log.d("SearchActivity", "${t.message}")
                            }

                            override fun onResponse(
                                call: Call<ProductList>,
                                response: Response<ProductList>
                            ) {
                                var adapter = SearchProductAdapter(
                                    this@SearchActivity,
                                    response.body()?.itemList!!)
                                val recyclerView: RecyclerView = findViewById(R.id.recycleview_search)
                                recyclerView.layoutManager = LinearLayoutManager(this@SearchActivity, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?
                                recyclerView.adapter = adapter
                                Log.d("SearchActivity", "${response.body()?.itemList}")

                            }

                        })
                }
                return true
            }

        })
            return true
    }


}
