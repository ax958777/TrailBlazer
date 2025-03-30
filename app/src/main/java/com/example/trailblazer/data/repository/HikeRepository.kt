package com.example.trailblazer.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.trailblazer.data.local.HikeDao
import com.example.trailblazer.data.local.TrackPointDao
import com.example.trailblazer.data.model.Hike
import com.example.trailblazer.data.model.TrackPoint
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject

class HikeRepository @Inject constructor(
    val hikeDao: HikeDao,
    val trackPointDao: TrackPointDao
) {

    fun getAllHikes(): Flow<List<Hike>> = hikeDao.getAllHikes()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun startNewHike(name:String):Long{
        val activeHike=hikeDao.getActiveHike()
        if(activeHike!=null){
            finishHike(activeHike.id)
        }
        val hike=Hike(
            name=name,
            startTime = Instant.now()
        )
        return  hikeDao.insertHike(hike)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun finishHike(hikeId:Long){
        val hike=hikeDao.getHikeById(hikeId) ?: return
        val updatedHike=hike.copy(endtime = Instant.now())
        hikeDao.updateHike(updatedHike)
    }

    suspend fun deleteHike(hikeId:Long){
        val hike=hikeDao.getHikeById(hikeId)?:return
        hikeDao.deleteHike(hike)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addTrackPoint(
        hikeId:Long,
        latitude:Double,
        longitude:Double,
        altitude:Double,
        accuracy:Float
    ){
        val trackPoint=TrackPoint(
            hikeId=hikeId,
            latitude=latitude,
            longitude=longitude,
            altitude=altitude,
            accuracy=accuracy,
            timestamp=Instant.now()
        )
        trackPointDao.insertTrackPoint(trackPoint)
    }

    suspend fun getActiveHike():Hike?=hikeDao.getActiveHike()

    suspend fun getLastTrackPoint(hikeId:Long):TrackPoint?{
        return trackPointDao.getLastTrackPoint(hikeId)
    }
}