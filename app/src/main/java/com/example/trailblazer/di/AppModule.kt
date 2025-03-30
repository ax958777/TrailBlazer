package com.example.trailblazer.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.trailblazer.data.local.HikeDao
import com.example.trailblazer.data.local.HikeDatabase
import com.example.trailblazer.data.local.TrackPointDao
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideHikeDatabase(app:Application):HikeDatabase{
        return Room.databaseBuilder(
            app,
            HikeDatabase::class.java,
            "hike_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideHikeDao(db:HikeDatabase):HikeDao{
        return db.hikeDao
    }

    @Provides
    @Singleton
    fun provideTrackpointDao(db:HikeDatabase):TrackPointDao{
        return db.trackPointDao
    }

    @Provides
    @Singleton
    fun provideFusedLocationProvider(@ApplicationContext context: Context):FusedLocationProviderClient{
        return LocationServices.getFusedLocationProviderClient(context)
    }

}