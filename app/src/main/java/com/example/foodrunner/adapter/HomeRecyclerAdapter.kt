package com.example.foodrunner.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.R
import com.example.foodrunner.activity.RestrauntMenuActivity
import com.example.foodrunner.database.RestrauntDatabase
import com.example.foodrunner.database.RestrauntEntity
import com.example.foodrunner.model.Restraunts
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context, val itemList:ArrayList<Restraunts>) : RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row,parent,false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restraunt=itemList[position]
        holder.txtRestrauntName.text=restraunt.restrauntName
        holder.txtPrice.text=restraunt.Price
        holder.txtRestrauntRating.text=restraunt.restrauntRating
        Picasso.get().load(restraunt.foodImage).error(R.drawable.foodrunner).into(holder.imgFood)

        val restrauntEntity=RestrauntEntity(
            restraunt.restrauntId.toInt(),
            restraunt.restrauntName,
            restraunt.Price,
            restraunt.restrauntRating,
            restraunt.foodImage
        )
        val checkFav=DBAsyncTask(context,restrauntEntity,1).execute()
        val isFav=checkFav.get()
        if(isFav) {
            holder.imgFav.setImageResource(R.drawable.ic_favourite)
        } else {
            holder.imgFav.setImageResource(R.drawable.ic_fav)
        }

        holder.imgFav.setOnClickListener {
            if(!DBAsyncTask(context,restrauntEntity,1).execute().get()) {
                val async=DBAsyncTask(context,restrauntEntity,2).execute()
                val result=async.get()
                if(result) {
                    holder.imgFav.setImageResource(R.drawable.ic_favourite)
                } else {
                    Toast.makeText(context,"Some Error occured",Toast.LENGTH_SHORT).show()
                }
            } else{
                val async=DBAsyncTask(context,restrauntEntity,3).execute()
                val result=async.get()
                if(result) {
                    holder.imgFav.setImageResource(R.drawable.ic_fav)
                } else {
                    Toast.makeText(context,"Some Error occured",Toast.LENGTH_SHORT).show()
                }
            }
        }
        holder.content.setOnClickListener{
            val intent= Intent(context,RestrauntMenuActivity::class.java)
            intent.putExtra("restraunt_id", restraunt.restrauntId)
            intent.putExtra("restraunt_name",restraunt.restrauntName)
            context.startActivity(intent)
        }
    }
    class HomeViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val txtRestrauntName:TextView=view.findViewById(R.id.txtRestrauntName)
        val txtPrice:TextView=view.findViewById(R.id.txtPrice)
        val txtRestrauntRating:TextView=view.findViewById(R.id.txtRestrauntRating)
        val imgFood:ImageView=view.findViewById(R.id.imgFood)
        val imgFav:ImageView=view.findViewById(R.id.imgFav)
        val content:RelativeLayout=view.findViewById(R.id.content)
    }
}

class DBAsyncTask(val context: Context, val restrauntEntity: RestrauntEntity, val mode:Int): AsyncTask<Void, Void, Boolean>() {
    val db= Room.databaseBuilder(context, RestrauntDatabase::class.java,"restraunt-db").build()
    override fun doInBackground(vararg p0: Void?): Boolean {
        when(mode) {
            1->{
                val restraunt: RestrauntEntity?=db.RestrauntDao().getRestrauntsById(restrauntEntity.restraunt_id.toString())
                db.close()
                return restraunt!=null
            }
            2->{
                db.RestrauntDao().insertRestraunt(restrauntEntity)
                db.close()
                return true
            }
            3->{
                db.RestrauntDao().deleteRestraunt(restrauntEntity)
                db.close()
                return true
            }
        }
        return false
    }

}