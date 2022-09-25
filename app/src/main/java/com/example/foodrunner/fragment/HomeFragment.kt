package com.example.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.adapter.HomeRecyclerAdapter
import com.example.foodrunner.database.RestrauntDatabase
import com.example.foodrunner.database.RestrauntEntity
import com.example.foodrunner.model.Restraunts
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class HomeFragment : Fragment() {
    lateinit var recyclerHome:RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progresslayout:RelativeLayout
    lateinit var progressBar: ProgressBar
    val restrauntsList= arrayListOf<Restraunts>()

    val ratingComparator=Comparator<Restraunts>{restraunt1 , restraunt2  ->
        if(restraunt1.restrauntRating.compareTo(restraunt2.restrauntRating,true)==0) {
            restraunt1.restrauntName.compareTo(restraunt2.restrauntName,true)
        }else {
            restraunt1.restrauntRating.compareTo(restraunt2.restrauntRating,true)
        }
    }
    val costComparator=Comparator<Restraunts> {restraunt1 , restraunt2 ->
        if(restraunt1.Price.compareTo(restraunt2.Price,true)==0) {
            restraunt1.restrauntName.compareTo(restraunt2.restrauntName,true)
        }else {
            restraunt1.Price.compareTo(restraunt2.Price,true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        recyclerHome=view.findViewById(R.id.recyclerHome)
        progresslayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        progresslayout.visibility=View.VISIBLE
        layoutManager=LinearLayoutManager(activity)

        val queue=Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v2/restaurants/fetch_result/"
        if(ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest=object:JsonObjectRequest(Request.Method.GET,url,null,Response.Listener {
                try {
                    progresslayout.visibility=View.GONE
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if(success) {
                        val resArray = data.getJSONArray("data")
                        for(i in 0 until resArray.length()) {
                            val restrauntJsonObject=resArray.getJSONObject(i)
                            val restrauntObject=Restraunts(
                                restrauntJsonObject.getString("id"),
                                restrauntJsonObject.getString("name"),
                                restrauntJsonObject.getString("rating"),
                                restrauntJsonObject.getString("cost_for_one"),
                                restrauntJsonObject.getString("image_url")
                            )
                            restrauntsList.add(restrauntObject)
                            recyclerAdapter= HomeRecyclerAdapter(activity as Context,restrauntsList)
                            recyclerHome.adapter=recyclerAdapter
                            recyclerHome.layoutManager=layoutManager
                        }
                    } else {
                        Toast.makeText(activity as Context,"Some Error occurred",Toast.LENGTH_SHORT).show()
                    }
                } catch(e:JSONException) {
                    Toast.makeText(activity as Context,"Some unexpected error occurred",Toast.LENGTH_SHORT).show()
                }
            },Response.ErrorListener {
                if(activity!=null) {
                    Toast.makeText(activity as Context,"Volley Error occurred!!",Toast.LENGTH_SHORT).show()
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
            val dialog=AlertDialog.Builder(activity as Context)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_home,menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item?.itemId
        if(id==R.id.action_sort) {
            val dialog=AlertDialog.Builder(activity as Context)
            dialog.setTitle("Sort By?")
            dialog.setPositiveButton("Cost(Low to High") { text,listener->
                Collections.sort(restrauntsList,costComparator)
                recyclerAdapter.notifyDataSetChanged()
            }
            dialog.setNegativeButton("Cost(High to Low)") {text,listener ->
                Collections.sort(restrauntsList,costComparator)
                restrauntsList.reverse()
                recyclerAdapter.notifyDataSetChanged()
            }
            dialog.setNeutralButton("Rating(High to Low)") {text,listener ->
                Collections.sort(restrauntsList,ratingComparator)
                restrauntsList.reverse()
                recyclerAdapter.notifyDataSetChanged()
            }
            dialog.create()
            dialog.show()
        }
        return super.onOptionsItemSelected(item)
    }
}
