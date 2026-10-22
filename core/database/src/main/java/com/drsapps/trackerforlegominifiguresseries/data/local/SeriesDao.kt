package com.drsapps.trackerforlegominifiguresseries.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.drsapps.trackerforlegominifiguresseries.domain.model.Series
import com.drsapps.trackerforlegominifiguresseries.domain.model.SeriesHiddenState
import com.drsapps.trackerforlegominifiguresseries.domain.model.SeriesIdAndName
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesDao {

    @Query("""
        SELECT * 
        FROM Series
        WHERE numOfMinifigsHidden != numOfMinifigs
        ORDER BY releaseDate DESC
    """)
    fun getVisibleSeries(): Flow<List<Series>>

    @Query("""
        SELECT *
        FROM Series
        WHERE numOfMinifigsHidden != numOfMinifigs AND favorite = 1
        ORDER BY releaseDate DESC
    """)
    fun getFavoriteSeries(): Flow<List<Series>> // Note: When the return type is Flow<List<T>>, querying an empty table emits an empty list.

    @Query("""
        SELECT *
        FROM Series
        WHERE numOfMinifigsHidden != numOfMinifigs
              AND  numOfMinifigsCollected = (numOfMinifigs - numOfMinifigsHidden)
        ORDER BY releaseDate DESC
    """)
    fun getCompletedSeries(): Flow<List<Series>> // Note: When the return type is Flow<List<T>>, querying an empty table emits an empty list.

    @Query("""
        SELECT *
        FROM Series
        WHERE numOfMinifigsHidden != numOfMinifigs
              AND numOfMinifigsCollected != (numOfMinifigs - numOfMinifigsHidden)
        ORDER BY releaseDate DESC
    """)
    fun getUncompletedSeries(): Flow<List<Series>>

    @Query("""
        SELECT imageUrl
        FROM Series
        WHERE id = :seriesId
    """)
    fun getSeriesImageUrl(seriesId: Int): String

    @Query("SELECT favorite FROM Series WHERE id = :seriesId")
    fun getSeriesFavoriteState(seriesId: Int): Flow<Boolean>

    @Query("UPDATE Series SET favorite = :favoriteState WHERE id = :seriesId")
    suspend fun setSeriesFavoriteState(seriesId: Int, favoriteState: Boolean)

    @Query("""
        SELECT *
        FROM Series
        WHERE numOfMinifigsHidden != numOfMinifigs
              AND name LIKE '%' || :query || '%'
        ORDER BY releaseDate ASC
    """) // Note: LIKE operator is case-insensitive
    fun getSeriesMatchingQuery(query: String): PagingSource<Int, Series> // Note: pagination only seems to work well for static data

    @Query("""
        SELECT id, name, numOfMinifigs, numOfMinifigsHidden
        FROM Series
        ORDER BY releaseDate DESC
    """)
    fun getHiddenStateOfAllSeries(): Flow<List<SeriesHiddenState>>

    @Query("""
        SELECT id, name
        FROM Series
        ORDER BY releaseDate DESC
    """)
    suspend fun getIdAndNameOfAllSeries(): List<SeriesIdAndName>

}