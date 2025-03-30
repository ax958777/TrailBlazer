package com.example.trailblazer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.Instant

@Entity(tableName = "locations")
@TypeConverters(InstantConverter::class)
data class TrackPoint(
    @PrimaryKey(autoGenerate = true)
    val id:Long=0,
    val hikeId:Long,
    val latitude:Double,
    val longitude:Double,
    val altitude:Double,
    val timestamp:Instant,
    val accuracy:Float
)
