package com.drsapps.trackerforlegominifiguresseries.presentation.minifigure_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureInventoryItem
import com.drsapps.trackerforlegominifiguresseries.domain.repository.AppUsageRepository
import com.drsapps.trackerforlegominifiguresseries.domain.repository.MinifigureInventoryItemRepository
import com.drsapps.trackerforlegominifiguresseries.domain.repository.MinifigureRepository
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Constants.MINIFIGURE_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MinifigureDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val minifigureRepo: MinifigureRepository,
    private val minifigureInventoryItemRepo: MinifigureInventoryItemRepository,
    private val appUsageRepo: AppUsageRepository
) : ViewModel() {

    private val id: Int = checkNotNull(savedStateHandle[MINIFIGURE_ID])

    val minifigure = minifigureRepo.getMinifigure(id)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    private val _minifigureParts = MutableStateFlow<List<MinifigureInventoryItem>>(emptyList())
    val minifigureParts = _minifigureParts.asStateFlow()

    private val _minifigureAccessories = MutableStateFlow<List<MinifigureInventoryItem>>(emptyList())
    val minifigureAccessories = _minifigureAccessories.asStateFlow()

    init {
        viewModelScope.launch {
            getMinifigurePartsAndAccessories(id)
        }
    }

    // TODO: Rename to getMinifigureInventoryItems?
    private suspend fun getMinifigurePartsAndAccessories(minifigureId: Int) {
        minifigureInventoryItemRepo.getMinifigureInventoryItems(minifigureId)
            .collect { minifigureInventoryItems ->
                _minifigureParts.value = minifigureInventoryItems.filter { it.type == "Part" }
                _minifigureAccessories.value = minifigureInventoryItems.filter { it.type == "Accessory" }
            }
    }

    fun toggleMinifigInvItemCollectedStateAndKeepMinifigCollectedStateInSync(
        minifigInvItemId: Int
    ) {
        viewModelScope.launch {
            minifigureInventoryItemRepo.toggleMinifigInvItemCollectedStateAndKeepMinifigCollectedStateInSync(minifigInvItemId, id)
            appUsageRepo.incrementSignificantActionsCount()
        }
    }

    fun toggleMinifigureCollectedStateAndResetWishlistedState() {
        if (minifigure.value != null) {
            viewModelScope.launch(Dispatchers.IO) {
                minifigureRepo.toggleMinifigureCollectedStateAndResetWishlistedState(id)
                appUsageRepo.incrementSignificantActionsCount()
            }
        }
    }

    fun toggleMinifigureWishlistedStateAndResetCollectedState() {
        if (minifigure.value != null) {
            viewModelScope.launch(Dispatchers.IO) {
                minifigureRepo.toggleMinifigureWishlistedStateAndResetCollectedState(id)
                appUsageRepo.incrementSignificantActionsCount()
            }
        }
    }

    fun toggleMinifigureFavoriteState() {
        if (minifigure.value != null) {
            viewModelScope.launch(Dispatchers.IO) {
                minifigureRepo.toggleMinifigureFavoriteState(id)
                appUsageRepo.incrementSignificantActionsCount()
            }
        }
    }

}