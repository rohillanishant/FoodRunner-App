package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.adapter.RestrauntMenuRecyclerAdapter
import com.example.foodrunner.database.FoodDatabase
import com.example.foodrunner.model.Menu
import com.example.foodrunner.model.Restraunts
import com.example.foodrunner.util.ConnectionManager

class RestrauntMenuActivity : AppCompatActivity() {
    lateinit var recyclerRestrauntmenu:RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter:RestrauntMenuRecyclerAdapter
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout:RelativeLayout
    lateinit var btnProceed:Button
    lateinit var rlProceed:RelativeLayout
    lateinit var toolbar:Toolbar
    var restraunt_id:String?=null
    var restrauntMenu= arrayListOf<Menu>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restraunt_menu)

        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title=intent.getStringExtra("restraunt_name")
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerRestrauntmenu=findViewById(R.id.recyclerRestrauntMenu)
        progressLayout=findViewById(R.id.progressLayout)
        progressBar=findViewById(R.id.progressBar)

        btnProceed=findViewById(R.id.btnProceed)
        rlProceed=findViewById(R.id.rlProceed)

        layoutManager=LinearLayoutManager(this@RestrauntMenuActivity)
        progressLayout.visibility= View.VISIBLE
        btnProceed.visibility=View.GONE
        if(intent!=null) {
            restraunt_id=intent.getStringExtra("restraunt_id")
        } else {
            Toast.makeText(this@RestrauntMenuActivity,"Some Unexpected error occurred",Toast.LENGTH_SHORT).show()
            finish()
        }
        val queue=Volley.newRequestQueue(this@RestrauntMenuActivity)
        val url="http://13.235.250.119/v2/restaurants/fetch_result/$restraunt_id"
        if(ConnectionManager().checkConnectivity(this@RestrauntMenuActivity)) {
            val jsonRequest=object: JsonObjectRequest(Request.Method.GET,url,null,Response.Listener {
                try {
                    val data=it.getJSONObject("data")
                    val success=data.getBoolean("success")
                    if(success) {
                        progressLayout.visibility=View.GONE
                        btnProceed.visibility=View.GONE
                        val menu=data.getJSONArray("data")
                        for(i in 0 until menu.length()) {
                            val menuJsonObject=menu.getJSONObject(i)
                            val menuObject=Menu(
                                menuJsonObject.getString("id"),
                                menuJsonObject.getString("name"),
                                menuJsonObject.getString("cost_for_one"),
                                menuJsonObject.getString("restaurant_id")
                            )
                            restrauntMenu.add(menuObject)
                            recyclerAdapter= RestrauntMenuRecyclerAdapter(this@RestrauntMenuActivity,
                                restraunt_id,
                                intent.getStringExtra("restraunt_name"),
                                rlProceed,//pass the relative layout which has the button to enable it later
                                btnProceed,
                                restrauntMenu)
                            recyclerRestrauntmenu.adapter=recyclerAdapter
                            recyclerRestrauntmenu.layoutManager=layoutManager
                        }
                    } else {
                        Toast.makeText(this@RestrauntMenuActivity,"Some Error Occurred!!",Toast.LENGTH_SHORT).show()
                    }
                } catch (e:Exception) {
                    Toast.makeText(this@RestrauntMenuActivity,"$e",Toast.LENGTH_SHORT).show()
                }
            },Response.ErrorListener {
                Toast.makeText(this@RestrauntMenuActivity,"Volley Error Occurred!!",Toast.LENGTH_SHORT).show()
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers=HashMap<String ,String>()
                    headers["content-type"]="application/json"
                    headers["token"]="ea418ea70c78af"
                    return headers
                }
            }
            queue.add(jsonRequest)
        } else {
            val dialog= AlertDialog.Builder(this@RestrauntMenuActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings"){ text,listener->
                val settingsIntent= Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(settingsIntent)
                this.finish()
            }
            dialog.setNegativeButton("Exit"){ text,listener->
                ActivityCompat.finishAffinity(this@RestrauntMenuActivity)
            }
            dialog.create()
            dialog.show()
        }

    }
    override fun onBackPressed() {
        if (recyclerAdapter.getSelectedItemCount() > 0) {
            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this@RestrauntMenuActivity)
            alterDialog.setTitle("Alert!")
            alterDialog.setMessage("Going back will remove everything from cart")
            alterDialog.setPositiveButton("Okay")
            { _, _ ->
                recyclerAdapter.itemSelectedCount=0
                if (DBAsyncTask(this@RestrauntMenuActivity, 0).execute().get()) {
                    //cleared
                    Toast.makeText(
                        this@RestrauntMenuActivity,
                        "Cart database cleared",
                        Toast.LENGTH_SHORT
                    ).show()
                    super.onBackPressed()
                } else {
                    Toast.makeText(
                        this@RestrauntMenuActivity,
                        "Cart database not cleared",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                super.onBackPressed()
            }
            alterDialog.setNegativeButton("No")
            { _, _ ->
                //do nothing
            }
            alterDialog.show()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
    }
    class DBAsyncTask(val context: Context, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        private val db = Room.databaseBuilder(context, FoodDatabase::class.java, "food-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                //Remove all items from cart
                0 -> {
                    db.FoodDao().clearCart()
                    db.close()
                    return true
                }
                //check if cart is empty or not
                1 -> {
                    val cartItems = db.FoodDao().getAllFoods()
                    db.close()
                    return cartItems.size > 0
                }
            }
            return false
        }
    }
}