package com.drsapps.trackerforlegominifiguresseries.data.repository

import com.drsapps.trackerforlegominifiguresseries.data.local.MinifigureInventoryItemDao
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureInventoryItem
import com.drsapps.trackerforlegominifiguresseries.domain.repository.MinifigureInventoryItemRepository
import kotlinx.coroutines.flow.Flow

class MinifigureInventoryItemRepositoryImpl(
    private val dao: MinifigureInventoryItemDao
) : MinifigureInventoryItemRepository {

    override suspend fun toggleMinifigInvItemCollectedStateAndKeepMinifigCollectedStateInSync(
        minifigInvItemId: Int,
        minifigureId: Int
    ) {
        dao.toggleMinifigInvItemCollectedStateAndKeepMinifigCollectedStateInSync(
            minifigInvItemId, minifigureId
        )
    }

    override fun getMinifigureInventoryItems(minifigureId: Int): Flow<List<MinifigureInventoryItem>> {
        return dao.getMinifigureInventoryItems(minifigureId)
    }

}