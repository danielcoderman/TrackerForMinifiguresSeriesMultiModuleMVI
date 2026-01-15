package com.drsapps.trackerforlegominifiguresseries.presentation.hide_minifigures

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureHiddenState
import com.drsapps.trackerforlegominifiguresseries.domain.model.SeriesIdAndName
import com.drsapps.trackerforlegominifiguresseries.domain.repository.AppUsageRepository
import com.drsapps.trackerforlegominifiguresseries.domain.repository.MinifigureRepository
import com.drsapps.trackerforlegominifiguresseries.domain.repository.SeriesRepository
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Constants.SELECTED_SERIES
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HideMinifiguresViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val minifigureRepo: MinifigureRepository,
    private val seriesRepo: SeriesRepository,
    private val appUsageRepo: AppUsageRepository
) : ViewModel() {

    private val _series = MutableStateFlow(emptyList<SeriesIdAndName>())
    val series = _series.asStateFlow()

    // I'm using mutableStateOf() instead of MutableStateFlow() because it allows saving a
    // custom object in savedStateHandle with the help of saveable().
    @OptIn(SavedStateHandleSaveableApi::class)
    var selectedSeries by savedStateHandle.saveable(
        key = SELECTED_SERIES,
        stateSaver = SeriesIdAndName.saver
    ) {
       mutableStateOf(SeriesIdAndName(-1, ""))
    }

    private val _minifiguresFromSelectedSeries = MutableStateFlow(emptyList<MinifigureHiddenState>())
    val minifiguresFromSelectedSeries = _minifiguresFromSelectedSeries.asStateFlow()

    private lateinit var getMinifiguresFromSelectedSeriesJob: Job

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _series.value = seriesRepo.getIdAndNameOfAllSeries()
        }

        // On process recreation, due to system-initiated process death, get the minifigures
        // corresponding to the restored selectedSeries.
        if (selectedSeries.id != -1 && selectedSeries.name != "") {
            getMinifiguresFromSelectedSeries(selectedSeries.id)
        }
    }

    fun changeSelectedSeriesAndMinifigures(seriesId: Int, seriesName: String) {
        selectedSeries = SeriesIdAndName(seriesId, seriesName)
        getMinifiguresFromSelectedSeries(seriesId)
    }

    fun deselectSeries() {
        selectedSeries = SeriesIdAndName(-1, "")
        getMinifiguresFromSelectedSeriesJob.cancel()
        _minifiguresFromSelectedSeries.value = emptyList()
    }

    fun toggleMinifigureHiddenState(minifigureId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            minifigureRepo.toggleMinifigureHiddenState(minifigureId)
            appUsageRepo.incrementSignificantActionsCount()
        }
    }

    private fun getMinifiguresFromSelectedSeries(seriesId: Int) {
        getMinifiguresFromSelectedSeriesJob = viewModelScope.launch {
            minifigureRepo.getAllMinifiguresFromSeries(seriesId)
                .flowOn(Dispatchers.IO)
                .collect { listOfMinifigures ->
                    _minifiguresFromSelectedSeries.value = listOfMinifigures
                }
        }
    }

}