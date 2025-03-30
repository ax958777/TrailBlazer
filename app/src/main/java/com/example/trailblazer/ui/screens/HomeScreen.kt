package com.example.trailblazer.ui.screens

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trailblazer.data.local.HikeDao
import com.example.trailblazer.data.model.Hike
import com.example.trailblazer.services.startLocationService
import com.example.trailblazer.ui.theme.ForestGreen
import com.example.trailblazer.ui.viewmodels.HomeViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToActiveHike: (Long)->Unit,
){
    val hikes by viewModel.hikes.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    var showNewHikeDialog by remember { mutableStateOf(false) }
    var newHikeName by remember { mutableStateOf("") }
    val scope= rememberCoroutineScope()
    val context= LocalContext.current

    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    LaunchedEffect(true) {
        // If there's active hike id , we need to navigate to active hike screen
        //onNavigateToActiveHike()
    }

    Scaffold(
        topBar = {
            TopAppBar(
            title = { Text("Trail Blazer") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = ForestGreen,
                titleContentColor = Color.White
            ))
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if(permissionState.allPermissionsGranted){
                        showNewHikeDialog=true
                    }else{
                        permissionState.launchMultiplePermissionRequest()
                    }
                },
                icon = { Icon( Icons.Filled.Add, "Add Hike")},
                text = { Text("New Hike")},
                containerColor = ForestGreen,
                contentColor = Color.White
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if(!permissionState.allPermissionsGranted){
                LocationPermissionRequest(permissionState)
            }
            if(isRefreshing){
                Box(
                    modifier=Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator()
                }

            }
            if(hikes.isEmpty()){
                EmptyHikeList()
            }else{
                HikeList(
                    hikes,
                    onHikeClick = onNavigateToActiveHike,
                    onDeleteHike = { hikeId->
                        scope.launch { viewModel.deleteHike(hikeId) }

                    }
                )
            }
            if(showNewHikeDialog){
                AlertDialog(
                    onDismissRequest = { showNewHikeDialog=false},
                    title = { Text("New Hike")},
                    text={
                        OutlinedTextField(
                            value=newHikeName,
                            onValueChange = {newHikeName=it},
                            label={Text("Hike Name")},
                            modifier=Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if(newHikeName.isNotBlank()){
                                    scope.launch{
                                        val hikeId=viewModel.startNewHike(newHikeName)
                                        startLocationService(context,hikeId)
                                        showNewHikeDialog=false
                                        newHikeName=""
                                        onNavigateToActiveHike(hikeId)
                                    }
                                }
                            }
                        ) {
                            Text("Start")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {showNewHikeDialog=false}) {Text("Cancel")}
                    }
                )
            }
        }

    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionRequest(permissionState:MultiplePermissionsState){
    Card(
        modifier=Modifier
            .fillMaxSize()
            .padding((16.dp)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Location Permission Required",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Trail Blazer needs location permission to track your hike",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {permissionState.launchMultiplePermissionRequest()},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Grant Permission")
            }
        }
    }
}

@Composable
fun EmptyHikeList(){
    Box(
        modifier=Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
          Icon(
              imageVector = Icons.Filled.DirectionsWalk,
              contentDescription = null,
              modifier = Modifier.size(64.dp),
              tint = ForestGreen
          )
          Spacer(modifier = Modifier.height(16.dp))
          Text(
              "No hikes recorded yet",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
          )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Tap the + button to start a new hike",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun HikeList(
    hikes:List<Hike>,
    onHikeClick:(Long)->Unit,
    onDeleteHike:(Long)->Unit
){
    LazyColumn(
        modifier=Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(hikes){ hike->
            HikeItem(
                hike=hike,
                onClick={onHikeClick(hike.id)},
                onDelete={onDeleteHike(hike.id)}
            )

        }
    }
}

@Composable
fun HikeItem(
    hike:Hike,
    onClick:()->Unit,
    onDelete:()->Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable (onClick=onClick ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape= RoundedCornerShape(12.dp)
    ){
        Column (
            modifier = Modifier.padding(16.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = hike.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    imageVector = Icons.Filled.Timer,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text =formatDate( hike.startTime),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

            }
        }
    }
}

private fun formatDate(instant:Instant):String{
    val formatter=DateTimeFormatter.ofPattern("MMM d, yyyy - HH:MM")
    val localDateTime=LocalDateTime.ofInstant(instant,ZoneId.systemDefault())
    return formatter.format(localDateTime)
}