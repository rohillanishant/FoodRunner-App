package com.example.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.foodrunner.model.Restraunts

@Dao
interface RestrauntDao {
    @Insert
    fun insertRestraunt(restrauntEntity: RestrauntEntity)

    @Delete
    fun deleteRestraunt(restrauntEntity: RestrauntEntity)

    @Query("SELECT * FROM restraunts")
    fun getAllRestraunts():List<RestrauntEntity>

    @Query("SELECT * FROM restraunts WHERE restraunt_id=:restrauntId")
    fun getRestrauntsById(restrauntId:String):RestrauntEntity
}