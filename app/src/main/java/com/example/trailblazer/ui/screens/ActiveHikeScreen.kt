package com.example.trailblazer.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trailblazer.ui.theme.ForestGreen
import com.example.trailblazer.ui.viewmodels.ActiveHikeViewModel
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveHikeScreen(
    viewModel:ActiveHikeViewModel= hiltViewModel(),
    onNavigationBack: () -> Unit
) {
    val currentLocation by viewModel.currentLocation.collectAsState()
    val cameraPositionState= rememberCameraPositionState()
    val mapProperties=MapProperties(mapType = MapType.TERRAIN, isMyLocationEnabled = true)
    val mapUiSettings=MapUiSettings(compassEnabled = true, myLocationButtonEnabled = true, zoomControlsEnabled = true)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Active Hike") },
                navigationIcon={
                    IconButton(onClick=onNavigationBack){
                        Icon(Icons.Filled.ArrowBack,"Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ForestGreen,
                    titleContentColor = Color.White
                ))
        }
    ){ paddingValues->
        Column(
            modifier=Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ){
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = mapProperties,
                    uiSettings = mapUiSettings
                ){
                    currentLocation?.let {
                        Marker(
                            state= MarkerState( position = it),
                            title="Current Location"
                        )
                    }

                }
            }
        }

    }
}