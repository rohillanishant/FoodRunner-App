package com.example.foodrunner.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.R
import com.example.foodrunner.adapter.HomeRecyclerAdapter
import com.example.foodrunner.database.RestrauntDatabase
import com.example.foodrunner.database.RestrauntEntity
import com.example.foodrunner.model.Restraunts


class FavouritesFragment : Fragment() {
    lateinit var recyclerFav: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter:HomeRecyclerAdapter
    lateinit var progresslayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    var dbRestaurantList=arrayListOf<Restraunts>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =inflater.inflate(R.layout.fragment_favourites, container, false)
        recyclerFav=view.findViewById(R.id.recyclerFav)
        progresslayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        progresslayout.visibility=View.VISIBLE
        layoutManager= LinearLayoutManager(activity)
        val restrauntList=RetrieveFavourites(activity as Context).execute().get()
        for(i in restrauntList) {
            dbRestaurantList.add(
                Restraunts(
                    i.restraunt_id.toString(),
                    i.restrauntName,
                    i.restrauntRating,
                    i.price,
                    i.foodImage
                )
            )
        }
        if(dbRestaurantList.isEmpty()) {
            Toast.makeText(context,"You have not selected any restraunt as your favourite",Toast.LENGTH_SHORT).show()
        }
        if(activity!=null) {
            progresslayout.visibility=View.GONE
            recyclerAdapter= HomeRecyclerAdapter(activity as Context,dbRestaurantList)
            recyclerFav.adapter=recyclerAdapter
            recyclerFav.layoutManager=layoutManager
        }
        return view
    }
    class RetrieveFavourites(val context: Context): AsyncTask<Void, Void, List<RestrauntEntity>>() {
        override fun doInBackground(vararg p0: Void?): List<RestrauntEntity> {
            val db= Room.databaseBuilder(context, RestrauntDatabase::class.java,"restraunt-db").build()
            return db.RestrauntDao().getAllRestraunts()
        }

    }
}