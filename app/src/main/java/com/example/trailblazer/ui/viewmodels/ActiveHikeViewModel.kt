package com.example.trailblazer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trailblazer.data.repository.HikeRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveHikeViewModel @Inject constructor(private val repository:HikeRepository):ViewModel(){

    private val _currentLocation= MutableStateFlow<LatLng?>(null)
    val currentLocation=_currentLocation.asStateFlow()

    private val _activeHikeId= MutableStateFlow<Long?>(null)
    val activeHikeId=_activeHikeId.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getActiveHike()?.let{ hike->
                _activeHikeId.value=hike.id
            }
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates(){
        viewModelScope.launch {
            while(_activeHikeId.value!=null) {
                activeHikeId.value?.let { hikeId ->
                    val lastTrackPoint = repository.getLastTrackPoint(hikeId)
                    lastTrackPoint?.let{ trackpoint->
                        _currentLocation.value=LatLng(trackpoint.latitude,trackpoint.longitude)

                    }
                                 }
                delay(50)
            }
        }
    }
}