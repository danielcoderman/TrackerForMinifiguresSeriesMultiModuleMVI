package com.drsapps.trackerforlegominifiguresseries.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.drsapps.trackerforlegominifiguresseries.domain.model.Minifigure
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureHiddenState
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureWithSeriesName
import kotlinx.coroutines.flow.Flow

@Dao
interface MinifigureDao {

    @Query(
        """
            SELECT Minifigure.id, Minifigure.name, Minifigure.imageUrl, Minifigure.positionInSeries,
                   Minifigure.seriesId, Minifigure.collected, Minifigure.wishListed,
                   Minifigure.favorite, Minifigure.hidden, Minifigure.numOfCollectedInventoryItems,
                   Minifigure.inventorySize
            FROM Minifigure
              INNER JOIN Series ON Series.id = Minifigure.seriesId
            WHERE Minifigure.hidden = 0
            ORDER BY Series.releaseDate DESC,
                     Minifigure.positionInSeries ASC
        """
    )
    // Gets all visible minifigures ordered by release date first and position in series second
    fun getVisibleMinifigures(): Flow<List<Minifigure>>

    @Query(
        """
            SELECT Minifigure.id, Minifigure.name, Minifigure.imageUrl, Minifigure.positionInSeries,
                   Minifigure.seriesId, Minifigure.collected, Minifigure.wishListed,
                   Minifigure.favorite, Minifigure.hidden, Minifigure.numOfCollectedInventoryItems,
                   Minifigure.inventorySize
            FROM Minifigure
              INNER JOIN Series ON Series.id = Minifigure.seriesId
            WHERE Minifigure.hidden = 0
                  AND Minifigure.favorite = 1
            ORDER BY Series.releaseDate DESC,
                     Minifigure.positionInSeries ASC
        """
    )
    // Gets all favorite minifigures ordered by release date first and position in series second.
    // Note: The extra expression in the WHERE clause checking if the minifigure is visible isn't
    // necessary, but it's used to enforce a rule and make clear the type of minifigures that are
    // being queried. There's a rule where when a minifigure is hidden it has several of its columns
    // set to default values (false value).
    fun getFavoriteMinifigures(): Flow<List<Minifigure>>

    @Query(
        """
            SELECT Minifigure.id, Minifigure.name, Minifigure.imageUrl, Minifigure.positionInSeries,
                   Minifigure.seriesId, Minifigure.collected, Minifigure.wishListed,
                   Minifigure.favorite, Minifigure.hidden, Minifigure.numOfCollectedInventoryItems,
                   Minifigure.inventorySize
            FROM Minifigure
              INNER JOIN Series ON Series.id = Minifigure.seriesId
            WHERE Minifigure.hidden = 0
                  AND Minifigure.collected = 1
                  AND Minifigure.wishListed = 0
            ORDER BY Series.releaseDate DESC,
                     Minifigure.positionInSeries ASC
        """
    )
    // Gets all collected minifigures ordered by release date first and position in series second.
    // Note: The extra expressions in the WHERE clause checking if the minifigure is visible and
    // not wishlisted aren't necessary, but they are used to enforce rules and make
    // clear the type of minifigures that are being queried. There are rules such as the wishlisted
    // and collected columns being mutually exclusive (both can't be set to true), and a minifigure
    // having several of its columns set to default values (false value) when its marked as hidden.
    fun getCollectedMinifigures(): Flow<List<Minifigure>>

    @Query(
        """
            SELECT Minifigure.id, Minifigure.name, Minifigure.imageUrl, Minifigure.positionInSeries,
                   Minifigure.seriesId, Minifigure.collected, Minifigure.wishListed,
                   Minifigure.favorite, Minifigure.hidden, Minifigure.numOfCollectedInventoryItems,
                   Minifigure.inventorySize
            FROM Minifigure
              INNER JOIN Series ON Series.id = Minifigure.seriesId
            WHERE Minifigure.hidden = 0
                  AND Minifigure.wishListed = 1
                  AND Minifigure.collected = 0  
            ORDER BY Series.releaseDate DESC,
                     Minifigure.positionInSeries ASC
        """
    )
    // Gets all wishlisted minifigures ordered by release date first and position in series second.
    // Note: The extra expressions in the WHERE clause checking if the minifigure is visible and if
    // it's not marked as collected aren't necessary, but they are used to enforce rules and make
    // clear the type of minifigures that are being queried. There are rules such as the wishlisted
    // and collected columns being mutually exclusive (both can't be set to true), and a minifigure
    // having several of its columns set to default values (false value) when its marked as hidden.
    fun getWishlistedMinifigures(): Flow<List<Minifigure>>

    @Query(
        """
            SELECT Minifigure.id, Minifigure.name, Minifigure.imageUrl, Minifigure.positionInSeries,
                   Minifigure.seriesId, Minifigure.collected, Minifigure.wishListed,
                   Minifigure.favorite, Minifigure.hidden, Minifigure.numOfCollectedInventoryItems,
                   Minifigure.inventorySize
            FROM Minifigure
              INNER JOIN Series ON Series.id = Minifigure.seriesId
            WHERE Minifigure.hidden = 0
                  AND Minifigure.collected = 0
            ORDER BY Series.releaseDate DESC,
                     Minifigure.positionInSeries ASC
        """
    )
    // Gets all minifigures that have not been collected, ordered by release date first and
    // position in series second.
    fun getUncollectedMinifigures(): Flow<List<Minifigure>>

    @Query("""
        SELECT Minifigure.id, Minifigure.name, Minifigure.imageUrl, Minifigure.collected,
               Minifigure.wishListed, Minifigure.favorite, Series.name AS seriesName
        FROM Minifigure
        INNER JOIN Series ON Series.id = Minifigure.seriesId
        WHERE Minifigure.id = :minifigureId
    """)
    // Note: There's no need to check if the minifigure is visible because this function will be
    // called for visible minifigures only.
    fun getMinifigure(minifigureId: Int): Flow<MinifigureWithSeriesName>

    @Query("""
        SELECT id, name, hidden
        FROM Minifigure
        WHERE seriesId = :seriesId
        ORDER BY positionInSeries
    """)
    fun getAllMinifiguresFromSeries(seriesId: Int): Flow<List<MinifigureHiddenState>>

    @Query("SELECT * FROM Minifigure " +
           "WHERE hidden = 0 AND " +
           "  seriesId = :seriesId " +
           "ORDER BY positionInSeries"
    )
    fun getVisibleMinifiguresFromSeries(seriesId: Int): Flow<List<Minifigure>>

    @Query("""
              UPDATE Minifigure
              SET collected = CASE
                                  WHEN collected = 0 THEN 1
                                  ELSE 0
                              END,
                  wishlisted = 0
              WHERE id = :minifigureId
    """)
    suspend fun toggleMinifigureCollectedStateAndResetWishlistedState(minifigureId: Int): Int

    @Query("""
              UPDATE Minifigure
              SET wishListed = CASE
                                  WHEN wishListed = 0 THEN 1
                                  ELSE 0
                              END,
                  collected = 0
              WHERE id = :minifigureId
    """)
    suspend fun toggleMinifigureWishlistedStateAndResetCollectedState(minifigureId: Int): Int

    @Query("""UPDATE Minifigure
              SET favorite = CASE
                                WHEN favorite = 0 THEN 1
                                ELSE 0
                             END     
              WHERE id = :minifigureId"""
    )
    // the return value represents the number of rows updated
    suspend fun toggleMinifigureFavoriteState(minifigureId: Int): Int

    @Query("""
        UPDATE Minifigure
        SET hidden = CASE
                         WHEN hidden = 0 THEN 1
                         ELSE 0
                     END
        WHERE id = :minifigureId
    """)
    // the return value represents the number of rows updated
    suspend fun toggleMinifigureHiddenState(minifigureId: Int): Int

    @Query("""
        SELECT Minifigure.id, Minifigure.name, Minifigure.imageUrl, Minifigure.collected,
               Minifigure.wishListed, Minifigure.favorite, Series.name AS seriesName
        FROM Minifigure
        INNER JOIN Series ON Series.id = Minifigure.seriesId
        WHERE Minifigure.hidden = 0
              AND Minifigure.name LIKE '%' || :query || '%'
        ORDER BY Series.releaseDate ASC,
                 Minifigure.positionInSeries ASC 
    """)
    fun getMinifiguresMatchingQuery(query: String): PagingSource<Int, MinifigureWithSeriesName>

    @Query("""UPDATE Minifigure
              SET hidden = 1
              WHERE seriesId = :seriesId
                    AND hidden = 0"""
    )
    // hides all the minifigures of a series
    suspend fun hideSeries(seriesId: Int): Int

    @Query("""UPDATE Minifigure
              SET hidden = 0
              WHERE seriesId = :seriesId"""
    )
    // unhides all the minifigures of a series
    suspend fun unhideSeries(seriesId: Int): Int

}