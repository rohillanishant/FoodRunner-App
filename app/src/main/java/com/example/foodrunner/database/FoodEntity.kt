package com.example.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class FoodEntity (
    @PrimaryKey val food_id:Int,
    @ColumnInfo(name="food_name") val foodName:String,
    @ColumnInfo(name="food_price") val foodPrice:String,
    @ColumnInfo(name="restraunt_name") val restrauntName:String
    )