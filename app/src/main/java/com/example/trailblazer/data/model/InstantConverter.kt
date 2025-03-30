package com.example.trailblazer.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.Instant

class InstantConverter {
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromInstant(instant:Instant?):Long?{
        return instant?.toEpochMilli()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toInstant(value:Long?):Instant?{
        return value?.let{Instant.ofEpochMilli(it)}
    }
}