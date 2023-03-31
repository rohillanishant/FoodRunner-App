package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.PrecomputedText
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.text.PrecomputedTextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.adapter.CartRecyclerAdapter
import com.example.foodrunner.adapter.DBAsyncTaskCart
import com.example.foodrunner.adapter.RestrauntMenuRecyclerAdapter
import com.example.foodrunner.database.FoodDatabase
import com.example.foodrunner.database.FoodEntity
import com.example.foodrunner.database.RestrauntDatabase
import com.example.foodrunner.database.RestrauntEntity
import com.example.foodrunner.model.Cart
import com.example.foodrunner.model.Menu
import com.example.foodrunner.util.ConnectionManager
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() , PaymentResultListener {
    lateinit var toolbar: Toolbar
    lateinit var txtRestrauntName:TextView
    lateinit var recyclerCart: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var sharedPreferences:SharedPreferences
    lateinit var btnOrder:Button
    var OrderPlaced : Boolean = false
    var dbCartList= arrayListOf<Cart>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        toolbar=findViewById(R.id.toolbar)
        txtRestrauntName=findViewById(R.id.txtRestrauntName)
        btnOrder=findViewById(R.id.btnOrder)
        setSupportActionBar(toolbar)
        supportActionBar?.title="My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerCart=findViewById(R.id.recyclerCart)
        layoutManager= LinearLayoutManager(this@CartActivity)

        val foodList=RetrieveCart(applicationContext).execute().get()
        for(i in foodList) {
            dbCartList.add(
                Cart(
                    i.food_id.toString(),
                    i.foodName,
                    i.foodPrice,
                    i.restrauntName
                )
            )
            recyclerAdapter= CartRecyclerAdapter(applicationContext,dbCartList)
            recyclerCart.adapter=recyclerAdapter
            recyclerCart.layoutManager=layoutManager
        }

        sharedPreferences=getSharedPreferences("FoodRunner Preferences", Context.MODE_PRIVATE)
        txtRestrauntName.text="Ordering from : " + intent.getStringExtra("restrauntName")

        val queue= Volley.newRequestQueue(this@CartActivity)
        val url="http://13.235.250.119/v2/place_order/fetch_result"
        val userId= sharedPreferences.getString("user_id", null)
        val restrauntId= intent.getStringExtra("restrauntId")
        val totalCost= intent.getIntExtra("total_cost",0)
        val cost=totalCost.toString()

        btnOrder.text="Place Order(Total Rs. ${cost.substring(1)} )"
        val jsonparam=JSONArray()
        for (foodItem in foodList) {
            val foodIdobj=JSONObject()
            foodIdobj.put("food_item_id",foodItem.food_id.toString())
            jsonparam.put(foodIdobj)
        }
        val jsonparams=JSONObject()
        jsonparams.put("user_id",userId)
        jsonparams.put("restaurant_id",restrauntId)
        jsonparams.put("total_cost",totalCost)
        jsonparams.put("food",jsonparam)
        btnOrder.setOnClickListener {
            DeleteCart(applicationContext).execute().get()
            if(ConnectionManager().checkConnectivity(this@CartActivity)) {
                val jsonRequest=object: JsonObjectRequest(Request.Method.POST,url,jsonparams, Response.Listener {
                    try {
                        val data=it.getJSONObject("data")
                        val success=data.getBoolean("success")
                        if(success) {
                            Toast.makeText(this@CartActivity,"Order Saved",Toast.LENGTH_SHORT).show()
                            OrderPlaced=true
                        } else {
                            val responseMessageServer = data.getString("errorMessage")
                            Toast.makeText(this, responseMessageServer.toString(), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e:Exception) {
                        Toast.makeText(this@CartActivity,"$e", Toast.LENGTH_SHORT).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(this@CartActivity,"Volley Error Occurred!!", Toast.LENGTH_SHORT).show()
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
                val dialog= AlertDialog.Builder(this@CartActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Open Settings"){ text,listener->
                    val settingsIntent= Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(settingsIntent)
                    this.finish()
                }
                dialog.setNegativeButton("Exit"){ text,listener->
                    ActivityCompat.finishAffinity(this@CartActivity)
                }
                dialog.create()
                dialog.show()
            }
            startPayment(totalCost)
        }

    }
    private fun startPayment(totalCost: Int) {
        /*
        *  You need to pass the current activity to let Razorpay create CheckoutActivity
        * */
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name","Food Runner")
            options.put("description","Demoing Charges")
            //You can omit the image option to fetch the image from the dashboard
            //options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
            options.put("theme.color", "#FC4C3B");
            options.put("currency","INR");
            var cost=totalCost.toString()
            cost+="00";
            options.put("amount", cost.substring(1))//pass amount in currency subunits

            val prefill = JSONObject()
            prefill.put("email","")
            prefill.put("contact","")

            options.put("prefill",prefill)
            co.open(this,options)
        }catch (e: Exception){
            Toast.makeText(this,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
    override fun onBackPressed() {
        DeleteCart(applicationContext).execute().get()
        val intent= Intent(this@CartActivity,HomeActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
    class RetrieveCart(val context: Context): AsyncTask<Void, Void, List<FoodEntity>>() {
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg p0: Void?): List<FoodEntity> {
            val db= Room.databaseBuilder(context, FoodDatabase::class.java,"food-db").build()
            return db.FoodDao().getAllFoods()
        }

    }
    class DeleteCart(val context: Context): AsyncTask<Void, Void, Boolean>() {
        val db= Room.databaseBuilder(context, FoodDatabase::class.java,"food-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            db.FoodDao().clearCart()
            db.close()
            return true
        }

    }

    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this@CartActivity,"Payment Successfull",Toast.LENGTH_SHORT).show()
        if(OrderPlaced){
            val intent=Intent(this@CartActivity,OrderActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this@CartActivity,"Payment Failed",Toast.LENGTH_SHORT).show()
    }
}