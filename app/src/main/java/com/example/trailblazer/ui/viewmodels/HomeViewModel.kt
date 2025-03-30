package com.example.trailblazer.ui.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trailblazer.data.repository.HikeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor( private val repository:HikeRepository) :ViewModel(){
    val hikes=repository.getAllHikes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    private val _isRefreshing= MutableStateFlow(false)
    val isRefreshing=_isRefreshing.asStateFlow()

    fun refreshHikes(){
        viewModelScope.launch {
            _isRefreshing.value=true
            //refresh data
            _isRefreshing.value=false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun startNewHike(name:String):Long{
        return repository.startNewHike(name)
    }

    suspend fun deleteHike(hikeId:Long){
        repository.deleteHike(hikeId)
    }
}