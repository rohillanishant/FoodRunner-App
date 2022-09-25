package com.example.foodrunner.adapter

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunner.R
import com.example.foodrunner.model.OrderHistory

class OrderHistoryRecyclerAdapter(val context: Context,val itemList:ArrayList<OrderHistory>) : RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderViewHolder>(){

    class OrderViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val txtRestrauntName:TextView=view.findViewById(R.id.txtRestrauntName)
        val txtOrderDate:TextView=view.findViewById(R.id.txtOrderDate)
        val txtTotalCost:TextView=view.findViewById(R.id.txtTotalCost)
        val txtOrderTime:TextView=view.findViewById(R.id.txtOrderTime)
        val llFood:LinearLayout=view.findViewById(R.id.llFood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_history_restraunt,parent,false)
        return OrderHistoryRecyclerAdapter.OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val food=itemList[position]
        holder.txtRestrauntName.text=food.restrauntName
        holder.txtOrderDate.text=food.orderDate.replace('-','/').subSequence(0,9)
        holder.txtTotalCost.text="Rs."+food.totalCost.removeRange(0,1)
        holder.txtOrderTime.text=food.orderDate.removeRange(0,9)
        for(i in 0 until food.foodName.size) {
            val inflater: LayoutInflater? =
                context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater?
            val llFoodItem =
                inflater?.inflate(R.layout.recycler_cart_single_row, null) as LinearLayout

            val txtFoodName: TextView = llFoodItem.findViewById(R.id.txtFoodname)

            val txtFoodPrice: TextView = llFoodItem.findViewById(R.id.txtPrice)

            val itemName = food.foodName[i]
            val itemCost = "Rs. ${food.foodPrice[i]}"
            txtFoodName.text = itemName
            txtFoodPrice.text = itemCost

            holder.llFood.addView(llFoodItem)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}