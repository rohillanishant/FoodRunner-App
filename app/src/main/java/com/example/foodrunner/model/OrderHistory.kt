package com.example.foodrunner.model

data class OrderHistory(
    val restrauntName:String,
    val orderDate:String,
    val totalCost:String,
    val foodName :ArrayList<String>,
    val foodPrice:ArrayList<String>
)
