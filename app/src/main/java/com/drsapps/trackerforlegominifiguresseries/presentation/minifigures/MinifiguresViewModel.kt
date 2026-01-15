package com.drsapps.trackerforlegominifiguresseries.presentation.minifigures

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drsapps.trackerforlegominifiguresseries.domain.model.Minifigure
import com.drsapps.trackerforlegominifiguresseries.domain.repository.MinifigureRepository
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Constants.FILTER
import com.drsapps.trackerforlegominifiguresseries.presentation.util.MinifiguresFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MinifiguresViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repo: MinifigureRepository
) : ViewModel() {

    val filter = savedStateHandle.getStateFlow(FILTER, MinifiguresFilter.ALL)

    private val _minifigures = MutableStateFlow(emptyList<Minifigure>())
    val minifigures = _minifigures.asStateFlow()

    private var getMinifiguresJob: Job? = null

    init {
        Log.d("MinifiguresViewModel", "Created")
        getMinifigures(filter.value)
    }

    fun changeFilterAndMinifigures(filter: MinifiguresFilter) {
        savedStateHandle[FILTER] = filter
        getMinifigures(filter)
    }

    // Non-urgent: Log the completion status of each flow below to know if it completed
    // successfully or not. The logging can occur in the onCompletion operator.
    private fun getMinifigures(filter: MinifiguresFilter) {
        getMinifiguresJob?.cancel()
        when (filter) {
            MinifiguresFilter.ALL -> {
                getMinifiguresJob = viewModelScope.launch {
                    repo.getVisibleMinifigures()
                        .flowOn(Dispatchers.IO)
                        .collect { minifigures ->
                            _minifigures.value = minifigures
                        }
                }
            }
            MinifiguresFilter.FAVORITES -> {
                getMinifiguresJob = viewModelScope.launch {
                    repo.getFavoriteMinifigures()
                        .flowOn(Dispatchers.IO)
                        .collect { minifigures ->
                            _minifigures.value = minifigures
                        }
                }
            }
            MinifiguresFilter.COLLECTED -> {
                getMinifiguresJob = viewModelScope.launch {
                    repo.getCollectedMinifigures()
                        .flowOn(Dispatchers.IO)
                        .collect { minifigures ->
                            _minifigures.value = minifigures
                        }
                }
            }
            MinifiguresFilter.WISHLIST -> {
                getMinifiguresJob = viewModelScope.launch {
                    repo.getWishlistedMinifigures()
                        .flowOn(Dispatchers.IO)
                        .collect { minifigures ->
                            _minifigures.value = minifigures
                        }
                }
            }
            MinifiguresFilter.UNCOLLECTED -> {
                getMinifiguresJob = viewModelScope.launch {
                    repo.getUncollectedMinifigures()
                        .flowOn(Dispatchers.IO)
                        .collect { minifigures ->
                            _minifigures.value = minifigures
                        }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("MinifiguresViewModel", "Destroyed")
    }

}