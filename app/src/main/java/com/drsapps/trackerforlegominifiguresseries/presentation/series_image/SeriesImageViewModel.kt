package com.drsapps.trackerforlegominifiguresseries.presentation.series_image

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drsapps.trackerforlegominifiguresseries.domain.repository.SeriesRepository
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Constants.SERIES_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeriesImageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    seriesRepo: SeriesRepository
) : ViewModel() {

    private val seriesId: Int = checkNotNull(savedStateHandle[SERIES_ID])

    private val _seriesImageUrl = MutableStateFlow("")
    val seriesImageUrl = _seriesImageUrl.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _seriesImageUrl.value = seriesRepo.getSeriesImageUrl(seriesId)
        }
    }

}