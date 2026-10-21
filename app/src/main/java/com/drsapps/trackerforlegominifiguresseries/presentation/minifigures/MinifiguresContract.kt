package com.drsapps.trackerforlegominifiguresseries.presentation.minifigures

import com.drsapps.trackerforlegominifiguresseries.domain.model.Minifigure
import com.drsapps.trackerforlegominifiguresseries.presentation.util.MinifiguresFilter

data class MinifiguresUiState(
    val minifigures: List<Minifigure> = emptyList(),
    val filter: MinifiguresFilter = MinifiguresFilter.ALL
)

sealed interface MinifiguresEvent {
    data class FilterChanged(val filter: MinifiguresFilter) : MinifiguresEvent
}

sealed interface MinifiguresEffect {
    // Nothing for now
}