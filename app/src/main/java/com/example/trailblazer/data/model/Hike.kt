package com.example.trailblazer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.Instant
@Entity(tableName = "hikes")
@TypeConverters(InstantConverter::class)
data class Hike(
    @PrimaryKey(autoGenerate = true)
    val id:Long=0,
    val name:String,
    val startTime : Instant,
    val endtime:Instant?=null,
    val distancemeters:Float=0f,
    val elevationGainMeters:Float=0f,
    val durationSeconds:Long=0,
    val imageUri:String?=null,
    val notes:String?=null
)
