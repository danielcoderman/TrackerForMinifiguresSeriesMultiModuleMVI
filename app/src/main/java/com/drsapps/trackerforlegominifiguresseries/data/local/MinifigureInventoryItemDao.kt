package com.drsapps.trackerforlegominifiguresseries.data.local

import android.util.Log
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureInventoryItem
import kotlinx.coroutines.flow.Flow

private const val TAG = "MinifigureInventoryItemDao"

@Dao
interface MinifigureInventoryItemDao {

    @Query("""
        SELECT *
        FROM MinifigureInventoryItem
        WHERE minifigureId = :minifigureId
    """)
    fun getMinifigureInventoryItems(minifigureId: Int): Flow<List<MinifigureInventoryItem>>

    // This method isn't meant to be called directly, so it shouldn't be exposed from the
    // corresponding repository.
    @Query("""
        UPDATE MinifigureInventoryItem
        SET collected = CASE
                            WHEN collected = 0 THEN 1
                            ELSE 0
                        END
        WHERE id = :minifigInvItemId
    """)
    suspend fun toggleMinifigureInventoryItemCollectedState(minifigInvItemId: Int): Int

    // This method isn't meant to be called directly, so it shouldn't be exposed from the
    // corresponding repository. For convenience purposes, even though this method is accessing
    // Minifigure data, it will be defined in the MinifigureInventoryItemDao because it's a little
    // more complicated to run a transaction that calls methods from different DAOs. Also, this
    // method and the one above go hand in hand (so it makes some sense to put it here) because
    // whenever a minifigure inventory item is collected/uncollected we must check if the
    // corresponding minifigure's collected state should change. In other words, the main action is
    // toggling the collected state of a minifigure inventory item so that's why this method and the
    // one above are defined in this DAO.
    @Query("""
        UPDATE Minifigure
        SET collected = CASE
                            WHEN numOfCollectedInventoryItems = inventorySize THEN 1
                            ELSE 0
                        END,
            wishlisted = CASE
                             WHEN numOfCollectedInventoryItems = inventorySize THEN 0
                             ELSE wishlisted
                         END
        WHERE id = :minifigureId
    """)
    suspend fun keepMinifigureCollectedStateInSync(minifigureId: Int)

    @Transaction
    suspend fun toggleMinifigInvItemCollectedStateAndKeepMinifigCollectedStateInSync(
        minifigInvItemId: Int,
        minifigureId: Int
    ) {
        val numOfRowsUpdated = toggleMinifigureInventoryItemCollectedState(minifigInvItemId)
        if (numOfRowsUpdated == 0) {
            Log.e(TAG, "toggleMinifigInvItemCollectedStateAndKeepMinifigCollectedStateInSync: No minifigure inventory item update occurred")
        } else {
            keepMinifigureCollectedStateInSync(minifigureId)
        }
    }

}