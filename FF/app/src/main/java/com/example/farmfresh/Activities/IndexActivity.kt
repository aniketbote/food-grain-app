package com.example.farmfresh.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.farmfresh.Database.CartDatabase
import com.example.farmfresh.Model.AllData
import com.example.farmfresh.Model.CartItem
import com.example.farmfresh.Model.OrderList
import com.example.farmfresh.R
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


lateinit var itemText:TextView
var emailHashGlobal: String = ""
lateinit var indexActivityGlobal: Intent

class IndexActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener{
    lateinit var featureImageList:List<String>
    lateinit var cartList:MutableList<CartItem>
    private var flag = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        indexActivityGlobal = this.intent
        setContentView(R.layout.activity_index)
        Log.d("IndexActivity", cartCount.toString())

        val db = CartDatabase(this)
        cartList = db.readData()
        Log.d("ProductActivity","$cartList")

        if(flag){
            if(cartList.size == 0){
                itemText.visibility = View.INVISIBLE
            }
            if(cartList.size > 0) {
                itemText.visibility = View.VISIBLE
                cartCount = cartList.size
                itemText.text = cartCount.toString()
            }
        }

        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        emailHashGlobal = token.getString("EMAILHASH", "").toString()
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


        val allDataObj = intent.getSerializableExtra("dataObj") as AllData
        featureImageList = allDataObj.featureList
        val exoticVegetable = allDataObj.itemList[0].name
        val count = allDataObj.totalHashMap.getValue("Exotic_Vegetables")
        Log.d("IndexActivity","${featureImageList[0]}")
        Log.d("IndexActivity","${exoticVegetable}")
        Log.d("IndexActivity","${count}")


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

        val carouselview:CarouselView = findViewById(R.id.carousel_index)
        carouselview.setPageCount(featureImageList.size)
        carouselview.setImageListener(imageListener)

        carouselview.setImageClickListener {
            Log.d("IndexActivity","Image: ${it} Clicked")
        }


            fruit_index.setSafeOnClickListener {
                    Log.d("IndexActivity", "Clicked Fruits")
                    val Ref = FirebaseDatabase.getInstance().getReference("/all_items/Fruits")
                    Ref
                    .orderByKey()
                    .limitToFirst(5)
                    .addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Log.d("IndexActivity", "Error Fetching Fruits Values")
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val itemList =
                                HelperUtils.getAllItemsList(p0)
                            Log.d("IndexActivity", "${itemList}")
                            val subDataObj =
                                HelperUtils.getCatObj(
                                    itemList,
                                    allDataObj.totalHashMap.getValue("Fruits")
                                )
                            val fruitIntent =
                                Intent(this@IndexActivity, ProductActivity::class.java)
                            fruitIntent.putExtra("subDataObj", subDataObj)
                            fruitIntent.putExtra("type", "Fruits")
                            startActivity(fruitIntent)
                        }


                    })

        }

        exoticfruits_index.setSafeOnClickListener {
            Log.d("Index Activity", "Clicked Exotic Fruits")
            val Ref = FirebaseDatabase.getInstance().getReference("/all_items/Exotic_Fruits")
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
                        val subDataObj =
                            HelperUtils.getCatObj(
                                itemList,
                                allDataObj.totalHashMap.getValue("Exotic_Fruits")
                            )
                        val exoticFruitIntent = Intent(this@IndexActivity, ProductActivity::class.java)
                        exoticFruitIntent.putExtra("subDataObj",subDataObj)
                        exoticFruitIntent.putExtra("type","Exotic_Fruits")
                        startActivity(exoticFruitIntent)
                    }

                })


        }
        vegetables_index.setSafeOnClickListener {
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
                        val subDataObj =
                            HelperUtils.getCatObj(
                                itemList,
                                allDataObj.totalHashMap.getValue("Vegetables")
                            )
                        val vegIntent = Intent(this@IndexActivity, ProductActivity::class.java)
                        vegIntent.putExtra("subDataObj",subDataObj)
                        vegIntent.putExtra("type","Vegetables")
                        startActivity(vegIntent)
                    }

                })
        }
        exoticveg_index.setSafeOnClickListener {
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
                        val subDataObj =
                            HelperUtils.getCatObj(
                                itemList,
                                allDataObj.totalHashMap.getValue("Exotic_Vegetables")
                            )
                        val exoticVegIntent = Intent(this@IndexActivity, ProductActivity::class.java)
                        exoticVegIntent.putExtra("subDataObj",subDataObj)
                        exoticVegIntent.putExtra("type","Exotic_Vegetables")
                        startActivity(exoticVegIntent)
                    }

                })
        }
        grain_index.setSafeOnClickListener {
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
                        val subDataObj =
                            HelperUtils.getCatObj(
                                itemList,
                                allDataObj.totalHashMap.getValue("Foodgrains")
                            )
                        val foodgrainIntent = Intent(this@IndexActivity, ProductActivity::class.java)
                        foodgrainIntent.putExtra("subDataObj",subDataObj)
                        foodgrainIntent.putExtra("type","Foodgrains")
                        startActivity(foodgrainIntent)
                    }

                })
        }





    }

    var imageListener: ImageListener = object : ImageListener
    {
        override fun setImageForPosition(position: Int, imageView: ImageView?) {
            if (imageView != null) {
                Glide.with(this@IndexActivity).load("${featureImageList[position]}").into(imageView)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cart_menu, menu)
        val count:View = menu!!.findItem(R.id.select_cart).actionView
        val icon=count.findViewById<ImageView>(R.id.cart_img)

        icon.setOnClickListener{
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
        flag = true
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search ->
            {
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
                Log.d("IndexActivity","Pressed Home Button")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            R.id.current_order -> {
                Log.d("IndexActivity","Pressed Current Orders")
                val currentRef = FirebaseDatabase.getInstance().getReference("all_orders/$emailHashGlobal/current")
                currentRef.addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("IndexActivity","Error occured: ${p0}")
                        return
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        Log.d("IndexActivity", "${p0}")
                        if(p0.value == null){
                            val pref = this@IndexActivity.getSharedPreferences(
                                "UserSharedPreferences",
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
                Log.d("IndexActivity","Pressed Previous Orders")
                val currentRef = FirebaseDatabase.getInstance().getReference("all_orders/$emailHashGlobal/previous")
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
                Log.d("IndexActivity","Pressed Support")
                val supportIntent = Intent(this, SupportActivity::class.java)
                startActivity(supportIntent)
            }

            R.id.logout -> {
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


}



