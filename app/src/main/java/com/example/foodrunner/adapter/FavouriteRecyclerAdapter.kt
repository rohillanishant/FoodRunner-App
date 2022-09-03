package com.example.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunner.R
import com.example.foodrunner.database.RestrauntEntity
import com.example.foodrunner.model.Restraunts
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(val context: Context,val restrauntList: List<RestrauntEntity>) : RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_fav_single_row,parent,false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restrauntList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val restraunt=restrauntList[position]
        holder.txtRestrauntName.text=restraunt.restrauntName
        holder.txtPrice.text=restraunt.price
        holder.txtRestrauntRating.text=restraunt.restrauntRating
        Picasso.get().load(restraunt.foodImage).error(R.drawable.foodrunner).into(holder.imgFood)

        val restrauntEntity=RestrauntEntity(
            restraunt.restraunt_id,
            restraunt.restrauntName,
            restraunt.price,
            restraunt.restrauntRating,
            restraunt.foodImage
        )

        holder.imgFav.setOnClickListener {
            Picasso.get().load(R.drawable.ic_fav)
            val async=DBAsyncTask(context,restrauntEntity,3).execute()
            val result=async.get()
            if(result) {
                holder.imgFav.setImageResource(R.drawable.ic_fav)
            } else {
                Toast.makeText(context,"Some Error occured",Toast.LENGTH_SHORT).show()
            }
        }
        holder.content.setOnClickListener{
            Toast.makeText(context,"Clicked on ${holder.txtRestrauntName.text}", Toast.LENGTH_SHORT).show()
        }
    }
    class FavouriteViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val txtRestrauntName: TextView =view.findViewById(R.id.txtRestrauntName)
        val txtPrice: TextView =view.findViewById(R.id.txtPrice)
        val txtRestrauntRating: TextView =view.findViewById(R.id.txtRestrauntRating)
        val imgFood: ImageView =view.findViewById(R.id.imgFood)
        val imgFav: ImageView =view.findViewById(R.id.imgFav)
        val content: RelativeLayout =view.findViewById(R.id.content)
    }
}
