package com.drsapps.trackerforlegominifiguresseries.presentation.series_details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drsapps.trackerforlegominifiguresseries.domain.model.UserPreferences
import com.drsapps.trackerforlegominifiguresseries.domain.repository.AppUsageRepository
import com.drsapps.trackerforlegominifiguresseries.domain.repository.MinifigureRepository
import com.drsapps.trackerforlegominifiguresseries.domain.repository.SeriesRepository
import com.drsapps.trackerforlegominifiguresseries.domain.repository.UserPreferencesRepository
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Constants.SERIES_ID
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Layout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeriesDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userPreferencesRepo: UserPreferencesRepository,
    private val minifigureRepo: MinifigureRepository,
    private val seriesRepo: SeriesRepository,
    private val appUsageRepo: AppUsageRepository
) : ViewModel() {

    private val seriesId: Int = checkNotNull(savedStateHandle[SERIES_ID])

    val isSeriesFavorite = seriesRepo.getSeriesFavoriteState(seriesId)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    val layout: StateFlow<Layout?> = userPreferencesRepo.getUserPreferences().map { userPreferences ->
        userPreferences.layout
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val minifigures = minifigureRepo.getVisibleMinifiguresFromSeries(seriesId)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    init {
        Log.d("SeriesDetailsViewModel", "Created")
    }

    fun toggleSeriesFavoriteState() {
        isSeriesFavorite.value?.let {
            viewModelScope.launch(Dispatchers.IO) {
                seriesRepo.setSeriesFavoriteState(seriesId, !it)
                appUsageRepo.incrementSignificantActionsCount()
            }
        }
    }

    fun toggleLayout() {
        layout.value?.let {
            viewModelScope.launch(Dispatchers.IO) {
                when (it) {
                    Layout.GRID -> userPreferencesRepo.setUserPreferences(UserPreferences(layout = Layout.LIST))
                    Layout.LIST -> userPreferencesRepo.setUserPreferences(UserPreferences(layout = Layout.GRID))
                }
            }
        }
    }

    fun toggleMinifigureCollectedStateAndResetWishlistedState(minifigureId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            minifigureRepo.toggleMinifigureCollectedStateAndResetWishlistedState(minifigureId)
            appUsageRepo.incrementSignificantActionsCount()
        }
    }

    fun toggleMinifigureWishlistedStateAndResetCollectedState(minifigureId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            minifigureRepo.toggleMinifigureWishlistedStateAndResetCollectedState(minifigureId)
            appUsageRepo.incrementSignificantActionsCount()
        }
    }

    fun toggleMinifigureFavoriteState(minifigureId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            minifigureRepo.toggleMinifigureFavoriteState(minifigureId)
            appUsageRepo.incrementSignificantActionsCount()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("SeriesDetailsViewModel", "Destroyed")
    }

}