package com.example.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restraunts")
data class RestrauntEntity(
    @PrimaryKey val restraunt_id:Int,
    @ColumnInfo val restrauntName:String,
    @ColumnInfo val price:String,
    @ColumnInfo val restrauntRating:String,
    @ColumnInfo val foodImage:String
)

