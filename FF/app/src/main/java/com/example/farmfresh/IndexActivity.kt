package com.example.farmfresh

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_index.*
import kotlinx.android.synthetic.main.activity_toolbar.*



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
        Log.d("IndexActivity","Name = ${name}")

        // value inside name variable to be displayed
        //Tried code
//        val name_header:TextView = findViewById(R.id.name_header_nav)
//        name_header.setText("${name}")

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


        navigationView.setNavigationItemSelectedListener(this)
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


