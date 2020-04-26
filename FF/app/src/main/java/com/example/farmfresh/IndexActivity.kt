package com.example.farmfresh

import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_index.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_toolbar.*
import kotlinx.android.synthetic.main.header_nav.*


class IndexActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener{

    lateinit var homeFragment: HomeFragment
    lateinit var currentOrdersFragment: CurrentOrdersFragment
    lateinit var previousOrdersFragment: PreviousOrdersFragment
    lateinit var supportFragment: SupportFragment
    lateinit var profileFragment: ProfileFragment
    lateinit var logOutFragment: LogOutFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        val name = token.getString("name","")
        val photoUrl = token.getString("imageUri","").toString()
        Log.d("IndexActivity","Name = ${name}")
        Log.d("IndexActivity","Name = ${photoUrl}")


        val nv:NavigationView = findViewById(R.id.nav_activity_index)
        val navView:View = nv.getHeaderView(0)
        val tv:TextView = navView.findViewById(R.id.name_header_nav)
        tv.setText("$name")

        val imageView:CircleImageView = navView.findViewById(R.id.showPhoto_header_nav)
        Glide.with(this).load("${photoUrl}").into(imageView)




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
        homeFragment = HomeFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout,homeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

        }

    override fun onNavigationItemSelected(MenuItem: MenuItem): Boolean {

        when (MenuItem.itemId)
        {

            R.id.home->{
                homeFragment = HomeFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout,homeFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.current_order -> {
                currentOrdersFragment = CurrentOrdersFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout,currentOrdersFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()

            }
            R.id.current_order -> {
                previousOrdersFragment = PreviousOrdersFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout,previousOrdersFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()

            }
            R.id.support -> {
                supportFragment = SupportFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout,supportFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()

            }
            R.id.myprofile -> {
                profileFragment = ProfileFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout,profileFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()

            }
            R.id.logout -> {
                logOutFragment = LogOutFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout,logOutFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()

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


