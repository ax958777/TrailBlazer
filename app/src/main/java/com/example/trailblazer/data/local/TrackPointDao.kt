package com.example.trailblazer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.trailblazer.data.model.TrackPoint
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackPointDao {
    @Query("SELECT * FROM locations WHERE hikeId= :hikeId ORDER BY timestamp ASC")
    fun getTrackPointsForHike(hikeId:Long): Flow<List<TrackPoint>>

    @Query("SELECT * FROM locations WHERE hikeId= :hikeId ORDER BY timestamp ASC")
    suspend fun  getTrackPointsForHikeSuspend(hikeId:Long): List<TrackPoint>

    @Insert
    suspend fun insertTrackPoint(trackPoint: TrackPoint):Long

    @Insert
    suspend fun insertTrackPoints(trackPoints: List<TrackPoint>)

    @Query("SELECT COUNT(*) FROM locations WHERE hikeId= :hikeId")
    suspend fun getTrackPointsCount(hikeId:Long): Int

    @Query("SELECT * FROM locations WHERE hikeId= :hikeId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastTrackPoint(hikeId:Long): TrackPoint?


}