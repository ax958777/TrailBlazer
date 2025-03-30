package com.example.trailblazer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.trailblazer.data.model.Hike
import kotlinx.coroutines.flow.Flow

@Dao
interface HikeDao {
    @Query("SELECT * FROM hikes ORDER BY startTime DESC")
    fun getAllHikes():Flow<List<Hike>>

    @Query("SELECT * FROM hikes WHERE id= :hikeId")
    suspend fun getHikeById(hikeId:Long):Hike?

    @Query("SELECT * FROM hikes WHERE id= :hikeId")
    fun getHikeByIdFlow(hikeId:Long):Flow<Hike?>

    @Insert
    suspend fun insertHike(hike:Hike):Long

    @Update
    suspend fun updateHike(hike:Hike)

    @Delete
    suspend fun deleteHike(hike:Hike)

    @Query("SELECT * FROM hikes WHERE endTime is NULL LIMIT 1")
    suspend fun getActiveHike():Hike?

}