package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foodrunner.R
import com.example.foodrunner.fragment.FaqsFragment
import com.example.foodrunner.fragment.FavouritesFragment
import com.example.foodrunner.fragment.HomeFragment
import com.example.foodrunner.fragment.ProfileFragment
import com.google.android.material.navigation.NavigationView

class NewActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    var previousMenuItem:MenuItem?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)
        drawerLayout=findViewById(R.id.drawerLayout)
        toolbar=findViewById(R.id.toolbar)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        frameLayout=findViewById(R.id.frameLayout)
        navigationView=findViewById(R.id.navigationView)
        setUpToolbar()
        val actionBarDrawerToggle=ActionBarDrawerToggle(
            this@NewActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)
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
                    supportActionBar?.title="ALL Restraunts"
                    drawerLayout.closeDrawers()
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, ProfileFragment()).commit()
                    supportActionBar?.title="My Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.fav -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, FavouritesFragment()).commit()
                    supportActionBar?.title="Favourite Restraunts"
                    drawerLayout.closeDrawers()
                }
                R.id.faq -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, FaqsFragment()).commit()
                    supportActionBar?.title="Frequently Asked Questions"
                    drawerLayout.closeDrawers()
                }
                R.id.logOut -> {
                    val intent= Intent(this@NewActivity,LoginActivity::class.java)
                    startActivity(intent)
                    sharedPreferences.edit().clear().apply()
                    finish()
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
        supportActionBar?.title="All Restraunts"
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