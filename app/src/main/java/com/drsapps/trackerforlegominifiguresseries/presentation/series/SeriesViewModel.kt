package com.drsapps.trackerforlegominifiguresseries.presentation.series

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drsapps.trackerforlegominifiguresseries.domain.model.Series
import com.drsapps.trackerforlegominifiguresseries.domain.repository.SeriesRepository
import com.drsapps.trackerforlegominifiguresseries.domain.use_case.SyncDataUseCase
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Constants.FILTER
import com.drsapps.trackerforlegominifiguresseries.presentation.util.SeriesFilter
import com.drsapps.trackerforlegominifiguresseries.util.SyncState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeriesViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val seriesRepo: SeriesRepository,
    private val syncDataUseCase: SyncDataUseCase
) : ViewModel() {

    val filter = savedStateHandle.getStateFlow(FILTER, SeriesFilter.ALL)

    private val _series = MutableStateFlow(emptyList<Series>())
    val series = _series.asStateFlow()

    // Tracks if there's an active refresh due to pull-to-refresh
    private val _isDataRefreshing = MutableStateFlow(false)
    val isDataRefreshing = _isDataRefreshing.asStateFlow()

    private val _refreshMessage = MutableStateFlow("")
    val refreshMessage = _refreshMessage.asStateFlow()

    private var getSeriesJob: Job? = null

    private var refreshDataJob: Job? = null

    init {
        Log.d("SeriesViewModel", "Created")
        getSeries(filter.value)
    }

    // This function fetches and saves the latest series and minifigure data
    // (minifigure inventory data included) from the REST API.
    // Note: The series flow above will pass any new series to collectors
    fun refreshData() {
        _isDataRefreshing.value = true
        refreshDataJob?.cancel()
        refreshDataJob = viewModelScope.launch {
            val syncResult = syncDataUseCase()
            if (syncResult is SyncState.Success) {
                _refreshMessage.value = "Successfully synced"
            } else if (syncResult is SyncState.NoNewData) {
                _refreshMessage.value = "You're all caught up!" // TODO("Consider showing a last synced timestamp or time period (Ex: 2 hrs ago)")
            } else if (syncResult is SyncState.Failure) {
                delay(50) // Future Update: [1]
                _refreshMessage.value = syncResult.message
            }
            _isDataRefreshing.value = false
        }
    }

    fun clearRefreshMessage() {
        _refreshMessage.value = ""
    }

    fun changeFilterAndSeries(filter: SeriesFilter) {
        savedStateHandle[FILTER] = filter
        getSeries(filter)
    }

    private fun getSeries(filter: SeriesFilter) {
        getSeriesJob?.cancel()
        when (filter) {
            SeriesFilter.ALL -> {
                getSeriesJob = viewModelScope.launch {
                    seriesRepo.getVisibleSeries()
                        .flowOn(Dispatchers.IO)
                        .collect { series ->
                            _series.value = series
                        }
                }
            }
            SeriesFilter.FAVORITES -> {
                getSeriesJob = viewModelScope.launch {
                    seriesRepo.getFavoriteSeries()
                        .flowOn(Dispatchers.IO)
                        .collect { series ->
                            _series.value = series
                        }
                }
            }
            SeriesFilter.COMPLETED -> {
                getSeriesJob = viewModelScope.launch {
                    seriesRepo.getCompletedSeries()
                        .flowOn(Dispatchers.IO)
                        .collect { series ->
                            _series.value = series
                        }
                }
            }
            SeriesFilter.UNCOMPLETED -> {
                getSeriesJob = viewModelScope.launch {
                    seriesRepo.getUncompletedSeries()
                        .flowOn(Dispatchers.IO)
                        .collect { series ->
                            _series.value = series
                        }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("SeriesViewModel", "Destroyed")
    }
}



// Notes
//
// [1]: The delay used here won't be necessary once I update to Material3 v1.4.0-alpha01 or later.
// In short, the delay is being used so that there's time for the PullToRefreshBox to get the `true`
// value for the isRefreshing property, before it receives the `false` value. If the data updates
// are emitted faster than the frame duration (Most device screens refresh 60 times per second) then
// compose will skip intermediate updates and only process the latest value for the next frame.
// Without the delay, for now, when a user pulls to refresh the indicator will stay visible
// and still (wheel isn't spinning). I encountered this issue when activating airplane mode because
// the syncing operation finishes quickly due to no internet access.
// https://www.droidcon.com/2025/04/30/at-the-mountains-of-madness-with-jetpack-compose/