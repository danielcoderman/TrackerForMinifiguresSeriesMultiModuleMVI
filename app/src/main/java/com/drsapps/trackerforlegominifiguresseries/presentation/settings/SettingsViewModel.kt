package com.drsapps.trackerforlegominifiguresseries.presentation.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(

) : ViewModel() {

    init {
        Log.d("SettingsViewModel", "Created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("SettingsViewModel", "Destroyed")
    }

}