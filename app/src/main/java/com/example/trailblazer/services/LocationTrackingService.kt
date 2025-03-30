package com.example.trailblazer.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import  android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.trailblazer.MainActivity
import com.example.trailblazer.R
import com.example.trailblazer.data.repository.HikeRepository
import com.example.trailblazer.services.LocationTrackingService.Companion.CHANNEL_ID
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationTrackingService:Service() {

    private val serviceScope= CoroutineScope(SupervisorJob()+Dispatchers.IO)

    @Inject
    lateinit var repository:HikeRepository

    @Inject
    lateinit var fusedLocationClient:FusedLocationProviderClient

    private lateinit var locationCallBack:LocationCallback

    private var isTracking=false
    private var activeHikeId:Long=-1

    companion object{
        private const val NOTIFICATION_ID=1
        private const val CHANNEL_ID="location_tracking_channel"

        const val ACTION_START_SERVICE="ACTION_START_SERVICE"
        const val EXTRA_HIKE_ID="EXTRA_HIKE_ID"
    }
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
    override fun onCreate(){
        super.onCreate()
        //println("Location Tracking Service is creating")
        locationCallBack=object :LocationCallback(){
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let{ location->
                    processLocation(location)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun processLocation(location:Location){
        if(!isTracking || activeHikeId==-1L) return
        serviceScope.launch {
            repository.addTrackPoint(
                hikeId = activeHikeId,
                latitude = location.latitude,
                longitude = location.longitude,
                altitude = location.altitude,
                accuracy = location.accuracy
            )
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let{
            when (it.action){
                ACTION_START_SERVICE -> {
                    val hikeId=it.getLongExtra(EXTRA_HIKE_ID,-1)
                    if(hikeId!=-1L){
                        startTracking(hikeId)
                    }
                }
            }
        }
        return START_STICKY
    }


    //@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    @SuppressLint("MissingPermissions")
    private fun startTracking(hikeId: Long){
        if(isTracking){
            return
        }
        activeHikeId=hikeId
        isTracking=true

        startForegroundService()

        // request Fused Location Provider to have a Location CallBack bind to a func
        // CAll Back func will recored track point to room db

        val locationRequest=LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,10000)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(15000)
            .build()
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallBack,
            Looper.getMainLooper()
        )
    }

    private fun startForegroundService(){
        //SEND NOTIFICATION
        startForeground(NOTIFICATION_ID,createNotification())
    }

    private fun createNotification():Notification{
        createNotificationChannel()
        val pendingIntent:PendingIntent=Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
            )
        }
            return NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Tracking Hike")
                .setContentText("Trailblazer is recording your hiking route")
                .setSmallIcon(R.drawable.ic_hiking)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

        }


    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Used for tracking hike location in background"
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}


// Companion function to start location service
fun startLocationService(context: Context,hikeId:Long) {
    val serviceIntent = Intent(context, LocationTrackingService::class.java).apply {
        action = LocationTrackingService.ACTION_START_SERVICE
        putExtra(LocationTrackingService.EXTRA_HIKE_ID, hikeId)
    }
    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
        context.startForegroundService(serviceIntent)
    }else{
        context.startService(serviceIntent)
    }
}