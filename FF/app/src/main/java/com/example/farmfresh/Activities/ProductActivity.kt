package com.example.farmfresh.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.farmfresh.Database.CartDatabase
import com.example.farmfresh.Model.SubData
import com.example.farmfresh.Adapters.ProductAdapter
import com.example.farmfresh.Model.OrderList
import com.example.farmfresh.R
import com.example.farmfresh.Utilities.HelperUtils
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_index.*
import kotlinx.android.synthetic.main.activity_toolbar.*

class ProductActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itemlist)

        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        val name = token.getString("name","")

        val nv: NavigationView = findViewById(R.id.nav_activity_index)
        val navView: View = nv.getHeaderView(0)

        nv.itemIconTintList = null


        val tv: TextView = navView.findViewById(R.id.name_header_nav)
        tv.setText("$name")

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


        val subDataObj = intent.getSerializableExtra("subDataObj") as SubData
        Log.d("ProductActivity","${subDataObj.itemList}")

        val type = intent.getStringExtra("type")
        Log.d("ProductActivity", "$type")

        val db = CartDatabase(this)
        val cartList = db.readData()
        Log.d("ProductActivity","$cartList")


        val recyclerView:RecyclerView = findViewById(R.id.recycleview)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?
        val adapter= ProductAdapter(
            subDataObj.itemList,
            cartList,
            type
        )
        recyclerView.adapter = adapter
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
                currentRef.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("IndexActivity","Error occured: ${p0}")
                        return
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        Log.d("IndexActivity", "${p0}")
                        if(p0.value == null){
                            val pref = this@ProductActivity.getSharedPreferences(
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
                        val currentOrdersIntent = Intent(this@ProductActivity, CurrentOrdersActivity::class.java)
                        currentOrdersIntent.putExtra("orderListObj",orderListObj)
                        startActivity(currentOrdersIntent)
                    }

                })
            }
            R.id.previous_orders -> {
                Log.d("IndexActivity","Pressed Previous Orders")
                val currentRef = FirebaseDatabase.getInstance().getReference("all_orders/$emailHashGlobal/previous")
                currentRef.addValueEventListener(object : ValueEventListener {
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
                        val previousOrdersIntent = Intent(this@ProductActivity, PreviousOrdersActivity::class.java)
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
}