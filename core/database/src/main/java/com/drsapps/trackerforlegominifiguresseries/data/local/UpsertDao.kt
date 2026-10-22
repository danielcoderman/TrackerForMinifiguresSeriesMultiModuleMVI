package com.drsapps.trackerforlegominifiguresseries.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.drsapps.trackerforlegominifiguresseries.domain.model.Minifigure
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureInventoryItem
import com.drsapps.trackerforlegominifiguresseries.domain.model.PartialMinifigure
import com.drsapps.trackerforlegominifiguresseries.domain.model.PartialMinifigureInventoryItem
import com.drsapps.trackerforlegominifiguresseries.domain.model.PartialSeries
import com.drsapps.trackerforlegominifiguresseries.domain.model.Series

@Dao
abstract class UpsertDao {

    @Transaction
    open suspend fun upsertAll(
        series: List<PartialSeries>,
        minifigures: List<PartialMinifigure>,
        minifigInventoryItems: List<PartialMinifigureInventoryItem>
    ) {
        insertOrUpdateSeries(series)
        insertOrUpdateMinifigures(minifigures)
        insertOrUpdateMinifigInventoryItems(minifigInventoryItems)

        // With the fix below, minifigure inventory item data can be added separately from
        // corresponding series and minifigure data, in the database. The reason being because
        // any potentially new minifigure inventory items that were fetched will have their
        // collected values set to true if their corresponding minifigures are collected.
        val itemIds = minifigInventoryItems.map { it.id }
        if (itemIds.isNotEmpty()) {
            itemIds
                .chunked(ITEM_IDS_CHUNK_SIZE)
                .forEach { itemIdsChunk ->
                    applyCollectedStatusToMinifigureInventoryItems(itemIdsChunk)
                }
        }
    }

    @Upsert(entity = Series::class)
    protected abstract suspend fun insertOrUpdateSeries(series: List<PartialSeries>)

    @Upsert(entity = Minifigure::class)
    protected abstract suspend fun insertOrUpdateMinifigures(minifigures: List<PartialMinifigure>)

    @Upsert(entity = MinifigureInventoryItem::class)
    protected abstract suspend fun insertOrUpdateMinifigInventoryItems(minifigInvItems: List<PartialMinifigureInventoryItem>)

    @Query("""
        UPDATE MinifigureInventoryItem
        SET collected = 1
        WHERE id IN (:itemIds)
          AND minifigureId IN (
            SELECT id FROM Minifigure WHERE collected = 1
        )
    """)
    protected abstract suspend fun applyCollectedStatusToMinifigureInventoryItems(itemIds: List<Int>)

    private companion object {
        private const val SQLITE_BIND_LIMIT = 999 // [1]
        private const val ITEM_IDS_CHUNK_SIZE = SQLITE_BIND_LIMIT
    }

}

// Notes
//
// [1]: As mentioned in the Query documentation on the Android developers website
// (https://developer.android.com/reference/androidx/room/Query), "...only 999 items can be bound
// to the query, this is a limitation of SQLite see Section 9 of SQLite Limits."