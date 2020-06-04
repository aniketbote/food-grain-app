package com.example.farmfresh.Activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.farmfresh.Adapters.PopularItemsAdapter
import com.example.farmfresh.Adapters.ProductAdapter
import com.example.farmfresh.Database.CartDatabase
import com.example.farmfresh.Model.*
import com.example.farmfresh.R
import com.example.farmfresh.Retrofit.RetrofitClient
import com.example.farmfresh.Utilities.HelperUtils
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_index.*
import kotlinx.android.synthetic.main.activity_toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


lateinit var itemText:TextView


class IndexActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener{
    var featureImageList:List<String> = mutableListOf()
    lateinit var padapter: PopularItemsAdapter
    lateinit var cartList:MutableList<CartItem>
    lateinit var carouselview:CarouselView
    lateinit var emailHash:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        HelperUtils.checkConnection(this)


        carouselview= findViewById(R.id.carousel_index)
        carouselview.setPageCount(featureImageList.size)
        carouselview.setImageListener(imageListener)

        // Fetching Data for carousel view
        val featureRef = FirebaseDatabase.getInstance().getReference("/featured")
        featureRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("LoadingActivity","Failed to retrieve feature list")
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("LoadingActivity", "Successfully Fetched featured data")

                val featureList = mutableListOf<String>()
                for (name in p0.children) {
                    Log.d("LoadingActivity", "Image Location : ${name.value.toString()}")
                    featureList.add(name.value.toString())
                }

                featureImageList = featureList
                carouselview.setPageCount(featureImageList.size)
                carouselview.setImageListener(imageListener)
            }
        })


        padapter = PopularItemsAdapter(this@IndexActivity, mutableListOf())
        Log.d("IndexActivity", cartCount.toString())

        val db = CartDatabase(this)
        cartList = db.readData()
        Log.d("IndexActivity","$cartList")



        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        emailHash = token.getString("EMAILHASH", "").toString()
        val name = token.getString("name","")
        val photoUrl = token.getString("imageUri","").toString()
        Log.d("IndexActivity","Name = ${name}")
        Log.d("IndexActivity","PhotoUrl = ${photoUrl}")


        val nv:NavigationView = findViewById(R.id.nav_activity_index)
        val navView:View = nv.getHeaderView(0)

        nv.itemIconTintList = null


        val tv:TextView = navView.findViewById(R.id.name_header_nav)
        tv.setText("$name")
        Log.d("IndexActivity","Name Set on Nav Bar")

        val imageView:CircleImageView = navView.findViewById(R.id.showPhoto_header_nav)
        Glide.with(this).load("${photoUrl}").into(imageView)
        Log.d("IndexActivity","Image Loaded On Nav Bar")




        tv.setOnClickListener {
            Log.d("IndexActivity","Pressed Profile Button : ${name}")
            val profileIntent = Intent(this, ProfileActivity::class.java)
            startActivity(profileIntent)
        }




        setSupportActionBar(toolbar)

        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            (R.string.open),
            (R.string.close)
        )
        {

        }

        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()



        nav_activity_index.setNavigationItemSelectedListener(this)



        carouselview.setImageClickListener {
            Log.d("IndexActivity", "Image: ${it} Clicked")
        }

        initRecyclerPopular()



        fruit_index.setSafeOnClickListener {
            HelperUtils.checkConnection(this)
            Log.d("IndexActivity", "Clicked Fruits")
            val Ref = FirebaseDatabase.getInstance().getReference("/all_items/Fruits")
            Ref
                .orderByKey()
                .limitToFirst(5)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("IndexActivity", "Error Fetching Fruits Values")
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val itemList = HelperUtils.getAllItemsList(p0)
                        Log.d("IndexActivity", "${itemList}")
                        val productListObj = HelperUtils.getCatObj(itemList)
                        val fruitIntent = Intent(this@IndexActivity, ProductActivity::class.java)
                        fruitIntent.putExtra("productListObj", productListObj)
                        startActivity(fruitIntent)
                    }
                })
        }

        exoticfruits_index.setSafeOnClickListener {
            HelperUtils.checkConnection(this)
            Log.d("Index Activity", "Clicked Exotic Fruits")
            val Ref = FirebaseDatabase.getInstance().getReference("/all_items/Exotic_Fruits") // aisa
            Ref
                .orderByKey()
                .limitToFirst(5)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("IndexActivity", "Error Fetching Exotic Fruits Values")
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val itemList =
                            HelperUtils.getAllItemsList(p0)
                        Log.d("IndexActivity","${itemList}")
                        val productListObj =
                            HelperUtils.getCatObj(
                                itemList
                            )
                        val exoticFruitIntent = Intent(this@IndexActivity, ProductActivity::class.java)
                        exoticFruitIntent.putExtra("productListObj",productListObj)
                        startActivity(exoticFruitIntent)
                    }
                })
        }

        vegetables_index.setSafeOnClickListener {
            HelperUtils.checkConnection(this)
            Log.d("Index Activity", "Clicked Vegetables")
            val Ref = FirebaseDatabase.getInstance().getReference("/all_items/Vegetables")
            Ref
                .orderByKey()
                .limitToFirst(5)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("IndexActivity", "Error Fetching Vegetables Values")
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val itemList =
                            HelperUtils.getAllItemsList(p0)
                        Log.d("IndexActivity","${itemList}")
                        val productListObj =
                            HelperUtils.getCatObj(
                                itemList)
                        val vegIntent = Intent(this@IndexActivity, ProductActivity::class.java)
                        vegIntent.putExtra("productListObj",productListObj)
                        startActivity(vegIntent)
                    }

                })
        }
        exoticveg_index.setSafeOnClickListener {
            HelperUtils.checkConnection(this)
            Log.d("Index Activity", "Clicked Exotic Vegetables")
            val Ref = FirebaseDatabase.getInstance().getReference("/all_items/Exotic_Vegetables")
            Ref
                .orderByKey()
                .limitToFirst(5)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("IndexActivity", "Error Fetching Exotic veg Values")
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val itemList =
                            HelperUtils.getAllItemsList(p0)
                        Log.d("IndexActivity","${itemList}")
                        val productListObj =
                            HelperUtils.getCatObj(
                                itemList)
                        val exoticVegIntent = Intent(this@IndexActivity, ProductActivity::class.java)
                        exoticVegIntent.putExtra("productListObj",productListObj)
                        startActivity(exoticVegIntent)
                    }

                })
        }
        grain_index.setSafeOnClickListener {
            HelperUtils.checkConnection(this)
            Log.d("Index Activity", "Clicked Food Grains")
            val Ref = FirebaseDatabase.getInstance().getReference("/all_items/Foodgrains")
            Ref
                .orderByKey()
                .limitToFirst(5)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("IndexActivity", "Error Fetching Foodgrain Values")
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val itemList =
                            HelperUtils.getAllItemsList(p0)
                        Log.d("IndexActivity","${itemList}")
                        val productListObj =
                            HelperUtils.getCatObj(
                                itemList)
                        val foodgrainIntent = Intent(this@IndexActivity, ProductActivity::class.java)
                        foodgrainIntent.putExtra("productListObj",productListObj)
                        startActivity(foodgrainIntent)
                    }

                })
        }


    }



    private fun initRecyclerPopular() {
        RetrofitClient.instance.popular()
            .enqueue(object : Callback<MutableList<Product>> {
                override fun onFailure(call: Call<MutableList<Product>>, t: Throwable) {

                    Toast.makeText(
                        this@IndexActivity,
                        "Failed : ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("IndexActivity", "Failed : ${t.message}")
                }

                override fun onResponse(
                    call: Call<MutableList<Product>>,
                    response: Response<MutableList<Product>>
                ) {
                    Log.d("LoadingActivity", "${response.body()}")
                    val finalList = response.body()
                    val recyclerView: RecyclerView = findViewById(R.id.recycler_index_exotic_fruits)
                    recyclerView.layoutManager = LinearLayoutManager(this@IndexActivity, RecyclerView.HORIZONTAL, false)
                    padapter = PopularItemsAdapter(this@IndexActivity, finalList!!)
                    recyclerView.adapter = padapter
                }
            })
    }


    var imageListener: ImageListener = object : ImageListener
    {
        override fun setImageForPosition(position: Int, imageView: ImageView?) {
            if (imageView != null && !featureImageList.isEmpty()) {
                val options = RequestOptions()
                    .placeholder(R.drawable.home)
                    .error(R.mipmap.ic_launcher)
                Glide
                    .with(this@IndexActivity)
                    .setDefaultRequestOptions(options)
                    .load("${featureImageList[position]}")
                    .into(imageView)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cart_menu, menu)
        val count:View = menu!!.findItem(R.id.select_cart).actionView
        val icon=count.findViewById<ImageView>(R.id.cart_img)
        Log.d("IndexActivity","${cartList.size}")

        icon.setOnClickListener{
            val db = CartDatabase(this)
            cartList = db.readData()
            Log.d("IndexActivity","${cartList.size}")
            HelperUtils.checkConnection(this)
            if(cartList.size == 0){
                Toast.makeText(this,"Nothing in the basket", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Log.d("Index Activity", "Clicked Add to cart button")
            val placeOrderIntent = Intent(this, PlaceOrderActivity::class.java)
            startActivity(placeOrderIntent)

        }



        if(cartList.size == 0){
            itemText = count.findViewById(
                R.id.item_count
            )
            itemText.visibility = View.INVISIBLE
        }
        if(cartList.size > 0) {
            itemText = count.findViewById(
                R.id.item_count
            )
            itemText.visibility = View.VISIBLE
            cartCount = cartList.size
            itemText.text = cartCount.toString()
        }
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search ->
            {
                HelperUtils.checkConnection(this)
                Log.d("Index Activity", "Clicked Search button")
                val searchIntent = Intent(this, SearchActivity::class.java)
                searchIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(searchIntent)
            }


        }
        return true
    }

    override fun onNavigationItemSelected(MenuItem: MenuItem): Boolean {
        when (MenuItem.itemId)
        {
            R.id.home ->{
                HelperUtils.checkConnection(this)
                Log.d("IndexActivity","Pressed Home Button")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            R.id.current_order -> {
                HelperUtils.checkConnection(this)
                Log.d("IndexActivity","Pressed Current Orders")
                val currentRef = FirebaseDatabase.getInstance().getReference("all_orders/$emailHash/current")
                currentRef.addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("IndexActivity","Error occured: ${p0}")
                        return
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        Log.d("IndexActivity", "${p0}")
                        if(p0.value == null){
                            val pref = this@IndexActivity.getSharedPreferences(
                                "${emailHash}",
                                Context.MODE_PRIVATE
                            )
                            val editor = pref.edit()
                            editor.putString("pendingOrder", false.toString())
                            editor.commit()
                        }
                        val orderList =
                            HelperUtils.getOrderList(p0)
                        val orderListObj =
                            OrderList(orderList)
                        Log.d("IndexActivity","${orderList}")
                        val currentOrdersIntent = Intent(this@IndexActivity, CurrentOrdersActivity::class.java)
                        currentOrdersIntent.putExtra("orderListObj",orderListObj)
                        startActivity(currentOrdersIntent)
                    }

                })
            }
            R.id.previous_orders -> {
                HelperUtils.checkConnection(this)
                Log.d("IndexActivity","Pressed Previous Orders")
                val currentRef = FirebaseDatabase.getInstance().getReference("all_orders/$emailHash/previous")
                    .orderByChild("OrderTime")
                    .limitToFirst(5)
                currentRef.addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("IndexActivity","Error occured: ${p0}")
                        return
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        Log.d("IndexActivity", "${p0}")
                        val orderList =
                            HelperUtils.getOrderList(p0)
                        val orderListObj =
                            OrderList(orderList)
                        Log.d("IndexActivity","${orderList}")
                        val previousOrdersIntent = Intent(this@IndexActivity, PreviousOrdersActivity::class.java)
                        previousOrdersIntent.putExtra("orderListObj",orderListObj)
                        startActivity(previousOrdersIntent)
                    }

                })
            }
            R.id.support -> {
                HelperUtils.checkConnection(this)
                Log.d("IndexActivity","Pressed Support")
                val supportIntent = Intent(this, SupportActivity::class.java)
                startActivity(supportIntent)
            }

            R.id.logout -> {
                HelperUtils.checkConnection(this)
                Log.d("IndexActivity","Pressed Log Out")
                val token = getSharedPreferences("UserSharedPreferences",Context.MODE_PRIVATE)
                val editor = token.edit()
                editor.putString("EMAILHASH","")
                editor.putString("email","")
                editor.putString("phone","")
                editor.putString("address","")
                editor.putString("name","")
                editor.putString("imageUri","")
                editor.commit()
                Log.d("IndexActivity","User Info Deleted from Shared preferences")
                val loginIntent = Intent(this,
                    LoginActivity::class.java)
                loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(loginIntent)
            }


        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        Log.d("Back","IndexActivity")
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {

            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else
        {
            super.onBackPressed()
        }

    }
    class SafeClickListener(
        private var defaultInterval: Int = 300,
        private val onSafeCLick: (View) -> Unit
    ) : View.OnClickListener {
        private var lastTimeClicked: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
                return
            }
            lastTimeClicked = SystemClock.elapsedRealtime()
            onSafeCLick(v)
        }
    }

    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

    override fun onRestart() {
//        initRecyclerPopular()
        padapter.notifyDataSetChanged()
        super.onRestart()
    }


}



