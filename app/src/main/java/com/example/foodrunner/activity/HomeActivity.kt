package com.example.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.core.view.marginTop
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foodrunner.R
import com.example.foodrunner.fragment.FaqsFragment
import com.example.foodrunner.fragment.FavouritesFragment
import com.example.foodrunner.fragment.HomeFragment
import com.example.foodrunner.fragment.ProfileFragment
import com.google.android.material.navigation.NavigationView
import java.util.*

class HomeActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var txtUserName:TextView
    lateinit var txtContactDetails:TextView
    var previousMenuItem:MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        drawerLayout=findViewById(R.id.drawerLayout)
        toolbar=findViewById(R.id.toolbar)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        frameLayout=findViewById(R.id.frameLayout)
        navigationView=findViewById(R.id.navigationView)
        val headerView = navigationView.getHeaderView(0)
        txtUserName = headerView.findViewById(R.id.txtUserName)
        txtContactDetails = headerView.findViewById(R.id.txtContactDetails)

        headerView.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, ProfileFragment()).commit()
            supportActionBar?.title="My Profile"
            drawerLayout.closeDrawers()
            navigationView.setCheckedItem(R.id.profile)
        }

        setUpToolbar()
        val actionBarDrawerToggle=ActionBarDrawerToggle(
            this@HomeActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)

        txtUserName.text=sharedPreferences.getString("name",null)
        txtContactDetails.text="+91-" + sharedPreferences.getString("mobile_number",null)

        openHome()
        navigationView.setNavigationItemSelectedListener {
            if(previousMenuItem!=null) {
                previousMenuItem?.isChecked=false
            }
            it.isCheckable=true
            it.isChecked=true
            previousMenuItem=it
            when(it.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, HomeFragment()).commit()
                    supportActionBar?.title="ALL Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, ProfileFragment()).commit()
                    supportActionBar?.title="My Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.fav -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, FavouritesFragment()).commit()
                    supportActionBar?.title="Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.orderHistory -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, OrderHistoryFragment()).commit()
                    supportActionBar?.title="Order History"
                    drawerLayout.closeDrawers()
                }
                R.id.faq -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, FaqsFragment()).commit()
                    supportActionBar?.title="Frequently Asked Questions"
                    drawerLayout.closeDrawers()
                }
                R.id.logOut -> {
                    drawerLayout.closeDrawers()
                    val dialog= AlertDialog.Builder(this@HomeActivity)
                    dialog.setTitle("Log Out?")
                    dialog.setMessage("Are you sure , you want to log out?")
                    dialog.setPositiveButton("Yes") { text,listener->
                        sharedPreferences.edit().clear().apply()
                        val intent= Intent(this@HomeActivity,LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    dialog.setNegativeButton("No") {text,listener ->
                    }
                    dialog.create()
                    dialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun openHome() {
        val fragment=HomeFragment()
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout,fragment)
        transaction.commit()
        supportActionBar?.title="All Restaurants"
        navigationView.setCheckedItem(R.id.home)
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title="Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val frag=supportFragmentManager.findFragmentById(R.id.frameLayout)
        when(frag) {
            !is HomeFragment-> openHome()
            else -> super.onBackPressed()
        }
    }
}