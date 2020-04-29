package com.example.farmfresh

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
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_index.*
import kotlinx.android.synthetic.main.activity_toolbar.*


class IndexActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener{



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        val name = token.getString("name","")
        val photoUrl = token.getString("imageUri","").toString()
        Log.d("IndexActivity","Name = ${name}")
        Log.d("IndexActivity","PhotoUrl = ${photoUrl}")


        val nv:NavigationView = findViewById(R.id.nav_activity_index)
        val navView:View = nv.getHeaderView(0)
        val tv:TextView = navView.findViewById(R.id.name_header_nav)
        tv.setText("$name")
        Log.d("IndexActivity","Name Set on Nav Bar")

        val imageView:CircleImageView = navView.findViewById(R.id.showPhoto_header_nav)
        Glide.with(this).load("${photoUrl}").into(imageView)
        Log.d("IndexActivity","Image Loaded On Nav Bar")

        val featuredImageListObj = intent.getSerializableExtra("featuredListObj") as featureLabels
        val featureImageList = featuredImageListObj.featureList
        Log.d("IndexActivity","${featureImageList[0]}")




        //Caruosel









        tv.setOnClickListener {
            Log.d("IndexActivity","Pressed Profile Button : ${name}")
            val profileIntent = Intent(this, ProfileActivity::class.java)
            startActivity(profileIntent)
        }






        setSupportActionBar(toolbar)
        val actionBar= supportActionBar
        actionBar?.title = "Farm Fresh"

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

    }

    override fun onNavigationItemSelected(MenuItem: MenuItem): Boolean {

        when (MenuItem.itemId)
        {
            R.id.home->{
                Log.d("IndexActivity","Pressed Home Button")
                startActivity(intent)
            }
            R.id.current_order -> {
                Log.d("IndexActivity","Pressed Current Orders")
                val currentOrdersIntent = Intent(this, CurrentOrdersActivity::class.java)
                startActivity(currentOrdersIntent)
            }
            R.id.previous_orders -> {
                Log.d("IndexActivity","Pressed Previous Orders")
                val previousOrdersIntent = Intent(this, PreviousOrdersActivity::class.java)
                startActivity(previousOrdersIntent)
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
                val loginIntent = Intent(this,LoginActivity::class.java)
                loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(loginIntent)
            }


        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)

        }
        else
        {
            super.onBackPressed()
        }

    }
}

