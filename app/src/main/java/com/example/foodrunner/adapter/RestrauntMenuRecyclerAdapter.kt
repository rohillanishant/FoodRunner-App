package com.example.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.R
import com.example.foodrunner.activity.CartActivity
import com.example.foodrunner.activity.RestrauntMenuActivity
import com.example.foodrunner.database.FoodDatabase
import com.example.foodrunner.database.FoodEntity
import com.example.foodrunner.database.RestrauntDatabase
import com.example.foodrunner.database.RestrauntEntity
import com.example.foodrunner.model.Menu
import com.example.foodrunner.model.Restraunts
import com.squareup.picasso.Picasso
import java.lang.Exception

class RestrauntMenuRecyclerAdapter(val context: Context,
                                   val restrauntId: String?,
                                   val restrauntName: String?,
                                   val proceedPassed: RelativeLayout,
                                   val btnProceed: Button,
                                   val itemList:ArrayList<Menu>)
    : RecyclerView.Adapter<RestrauntMenuRecyclerAdapter.RestrauntMenuViewHolder>(){

    var itemSelectedCount: Int = 0
    lateinit var proceedToCart: RelativeLayout
    var totalCost:Int?=0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestrauntMenuViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_restraunt_menu_single_row,parent,false)
        return RestrauntMenuViewHolder(view)
    }

    fun getSelectedItemCount(): Int {
        return itemSelectedCount
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RestrauntMenuViewHolder, position: Int) {
        val menu=itemList[position]
        proceedToCart=proceedPassed //Proceed button passed from activity to adapter
        //holder.btnAdd.setTag(menu.foodId + "")//save the item id in textViewName Tag ,will be used to add to cart
        holder.txtFoodName.text=menu.foodName
        holder.txtPrice.text="Rs."+menu.Price
        holder.txtFoodNo.text= (position+1).toString()

        val foodEntity=FoodEntity(
            menu.foodId.toInt(),
            menu.foodName,
            menu.Price,
            menu.restrauntName
        )

        btnProceed.setOnClickListener(View.OnClickListener
        {
            val intent = Intent(context, CartActivity::class.java)
            intent.putExtra("restrauntId", restrauntId.toString())
            intent.putExtra("restrauntName", restrauntName)
            intent.putExtra("total_cost",totalCost)
            context.startActivity(intent)
            val checkAdd=DBAsyncTaskCart(context,foodEntity,1).execute().get()
            if(checkAdd){
                holder.btnAdd.text = "Remove"
                val color=ContextCompat.getColor(context, R.color.yellow)
                holder.btnAdd.setBackgroundColor(color)
            } else {
                holder.btnAdd.text = "Add"
                val color=ContextCompat.getColor(context,R.color.app_color)
                holder.btnAdd.setBackgroundColor(color)
            }
            totalCost=0
        })
        holder.btnAdd.setOnClickListener{
            if(!DBAsyncTaskCart(context,foodEntity,1).execute().get()) {
                val async=DBAsyncTaskCart(context,foodEntity,2).execute()
                val result=async.get()
                if(result) {
                    itemSelectedCount++ //item selected
                    Toast.makeText(context,"items selected : $itemSelectedCount",Toast.LENGTH_SHORT).show()
                    holder.btnAdd.text = "Remove"
                    val color=ContextCompat.getColor(context, R.color.yellow)
                    holder.btnAdd.setBackgroundColor(color)
                    try {
                        totalCost= totalCost?.minus(menu.Price.toInt())
                    }catch (e:Exception) {
                        Toast.makeText(context,"$e",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context,"Some Error occured", Toast.LENGTH_SHORT).show()
                }
            } else{
                val async=DBAsyncTaskCart(context,foodEntity,3).execute()
                val result=async.get()
                if(result) {
                    itemSelectedCount--     //item unselected
                    holder.btnAdd.text = "Add"
                    val color=ContextCompat.getColor(context,R.color.app_color)
                    holder.btnAdd.setBackgroundColor(color)
                    try {
                        totalCost = totalCost?.plus(menu.Price.toInt())
                    }catch (e:Exception) {
                        Toast.makeText(context,"$e",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context,"Some Error occured", Toast.LENGTH_SHORT).show()
                }
            }

            if (itemSelectedCount > 0) {
                btnProceed.visibility=View.VISIBLE
            } else {
                btnProceed.visibility=View.GONE
            }
        }
    }
    class RestrauntMenuViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val txtFoodNo:TextView=view.findViewById(R.id.txtFoodNo)
        val txtFoodName:TextView=view.findViewById(R.id.txtFoodname)
        val txtPrice:TextView=view.findViewById(R.id.txtPrice)
        val btnAdd:Button=view.findViewById(R.id.btnAdd)
    }
}
class DBAsyncTaskCart(val context: Context, val foodEntity: FoodEntity, val mode:Int): AsyncTask<Void, Void, Boolean>() {
    val db= Room.databaseBuilder(context, FoodDatabase::class.java,"food-db").build()
    override fun doInBackground(vararg p0: Void?): Boolean {
        when(mode) {
            1->{
                val food: FoodEntity?=db.FoodDao().getFoodById(foodEntity.food_id.toString())
                db.close()
                return food!=null
            }
            2->{
                db.FoodDao().insertFood(foodEntity)
                db.close()
                return true
            }
            3->{
                db.FoodDao().deleteFood(foodEntity)
                db.close()
                return true
            }
        }
        return false
    }

}