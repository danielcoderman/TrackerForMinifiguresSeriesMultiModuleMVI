package com.drsapps.trackerforlegominifiguresseries.domain.repository

import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureInventoryItem
import kotlinx.coroutines.flow.Flow

interface MinifigureInventoryItemRepository {

    suspend fun toggleMinifigInvItemCollectedStateAndKeepMinifigCollectedStateInSync(
        minifigInvItemId: Int,
        minifigureId: Int
    )

    fun getMinifigureInventoryItems(minifigureId: Int): Flow<List<MinifigureInventoryItem>>

}