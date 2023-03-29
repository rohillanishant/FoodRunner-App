package com.example.foodrunner.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.adapter.CartRecyclerAdapter
import com.example.foodrunner.adapter.HomeRecyclerAdapter
import com.example.foodrunner.adapter.OrderHistoryRecyclerAdapter
import com.example.foodrunner.model.OrderHistory
import com.example.foodrunner.model.Restraunts
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONException

class OrderHistoryFragment : Fragment() {
    lateinit var recyclerOrder: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderHistoryRecyclerAdapter
    lateinit var sharedPreferences: SharedPreferences
    val foodList= arrayListOf<OrderHistory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_order_history, container, false)
        recyclerOrder=view.findViewById(R.id.recyclerOrder)
        layoutManager= LinearLayoutManager(activity)
        sharedPreferences=(activity as FragmentActivity).getSharedPreferences("FoodRunner Preferences", Context.MODE_PRIVATE)
        val userId=sharedPreferences.getString("user_id",null)
        val queue= Volley.newRequestQueue(context)
        val url="http://13.235.250.119/v2/orders/fetch_result/$userId"
        if(ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest=object: JsonObjectRequest(Request.Method.GET,url,null, Response.Listener {
                try {
                    //progresslayout.visibility=View.GONE
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if(success) {
                        val resArray = data.getJSONArray("data")
                        for(i in 0 until resArray.length()) {
                            val restrauntJsonObject=resArray.getJSONObject(i)
                            val foodItems= ArrayList<String>()
                            val foodCosts=ArrayList<String>()
                            val foodArray=restrauntJsonObject.getJSONArray("food_items")
                            for(j in 0 until foodArray.length()) {
                                val foodJsonobject=foodArray.getJSONObject(j)
                                foodItems.add(foodJsonobject.getString("name"))
                                foodCosts.add(foodJsonobject.getString("cost"))
                            }
                            val orderObject=OrderHistory(
                                restrauntJsonObject.getString("restaurant_name"),
                                restrauntJsonObject.getString("order_placed_at"),
                                restrauntJsonObject.getString("total_cost"),
                                foodItems,
                                foodCosts
                            )
                            foodList.add(orderObject)
                            recyclerAdapter= OrderHistoryRecyclerAdapter(activity as Context,foodList)
                            recyclerOrder.adapter=recyclerAdapter
                            recyclerOrder.layoutManager=layoutManager
                        }
                    } else {
                        Toast.makeText(activity as Context,"Some Error occurred", Toast.LENGTH_SHORT).show()
                    }
                } catch(e: JSONException) {
                    Toast.makeText(activity as Context,"Some unexpected error occurred", Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                if(activity!=null) {
                    Toast.makeText(activity as Context,"Volley Error occurred!!", Toast.LENGTH_SHORT).show()
                }
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers =HashMap<String,String>()
                    headers["Content-type"]="application/json"
                    headers["token"]="ea418ea70c78af"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        } else {
            val dialog= AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet connection is not found")
            dialog.setPositiveButton("Open settings") { text,listener->
                val intent= Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(intent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit app") {text,listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }
}