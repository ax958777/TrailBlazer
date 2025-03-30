package com.example.trailblazer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.trailblazer.data.model.Hike
import com.example.trailblazer.data.model.TrackPoint

@Database(entities = [Hike::class,TrackPoint::class],version=1)
abstract class HikeDatabase:RoomDatabase() {
    abstract val hikeDao: HikeDao
    abstract val trackPointDao: TrackPointDao
}