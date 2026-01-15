package com.drsapps.trackerforlegominifiguresseries.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drsapps.trackerforlegominifiguresseries.domain.repository.AppStateRepository
import com.drsapps.trackerforlegominifiguresseries.domain.use_case.SyncDataUseCase
import com.drsapps.trackerforlegominifiguresseries.util.SyncState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val syncDataUseCase: SyncDataUseCase,
    private val appStateRepo: AppStateRepository
) : ViewModel() {

    private val _syncDataState = MutableStateFlow<SyncState>(SyncState.Loading)
    val syncDataState = _syncDataState.asStateFlow()

    private var syncDataJob: Job? = null

    init {
        syncData()
    }

    private fun syncData() {
        // Note that the coroutine launched from the CoroutineScope associated with this ViewModel
        // will cancel automatically when the ViewModel is cleared. Otherwise any launched
        // coroutines aren't cancelled unless they're manually cancelled. We only need one coroutine
        // running at a time for syncing the data, therefore we will cancel any existing
        // coroutine that was launched before launching a new one.
        syncDataJob?.cancel()
        syncDataJob = viewModelScope.launch {
            _syncDataState.value = syncDataUseCase()
            if (_syncDataState.value is SyncState.Success) {
                appStateRepo.updateHasExistingDataValue(true)
            }
        }
    }

    fun retry() {
       _syncDataState.value = SyncState.Loading
        syncData()
    }

}