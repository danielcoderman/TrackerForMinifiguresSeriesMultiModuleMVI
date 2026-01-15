package com.drsapps.trackerforlegominifiguresseries.presentation.hide_series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drsapps.trackerforlegominifiguresseries.domain.model.SeriesHiddenState
import com.drsapps.trackerforlegominifiguresseries.domain.repository.AppUsageRepository
import com.drsapps.trackerforlegominifiguresseries.domain.repository.MinifigureRepository
import com.drsapps.trackerforlegominifiguresseries.domain.repository.SeriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HideSeriesViewModel @Inject constructor(
    private val seriesRepo: SeriesRepository,
    private val minifigureRepo: MinifigureRepository,
    private val appUsageRepo: AppUsageRepository
) : ViewModel() {

    private val _series = MutableStateFlow(emptyList<SeriesHiddenState>())
    val series = _series.asStateFlow()

    init {
        viewModelScope.launch {
            seriesRepo.getHiddenStateOfAllSeries()
                .flowOn(Dispatchers.IO)
                .collect { series ->
                    _series.value = series
                }
        }
    }

    fun hideSeries(seriesId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            minifigureRepo.hideSeries(seriesId)
            appUsageRepo.incrementSignificantActionsCount()
        }
    }

    fun unhideSeries(seriesId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            minifigureRepo.unhideSeries(seriesId)
            appUsageRepo.incrementSignificantActionsCount()
        }
    }

}