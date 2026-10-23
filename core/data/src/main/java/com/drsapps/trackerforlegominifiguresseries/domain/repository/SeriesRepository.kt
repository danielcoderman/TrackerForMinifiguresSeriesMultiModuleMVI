package com.drsapps.trackerforlegominifiguresseries.domain.repository

import androidx.paging.PagingSource
import com.drsapps.trackerforlegominifiguresseries.domain.model.Series
import com.drsapps.trackerforlegominifiguresseries.domain.model.SeriesHiddenState
import com.drsapps.trackerforlegominifiguresseries.domain.model.SeriesIdAndName
import kotlinx.coroutines.flow.Flow

interface SeriesRepository {

    fun getVisibleSeries(): Flow<List<Series>>

    fun getFavoriteSeries(): Flow<List<Series>>

    fun getCompletedSeries(): Flow<List<Series>>

    fun getUncompletedSeries(): Flow<List<Series>>

    suspend fun getSeriesImageUrl(seriesId: Int): String

    fun getSeriesFavoriteState(seriesId: Int): Flow<Boolean>

    suspend fun setSeriesFavoriteState(seriesId: Int, favoriteState: Boolean)

    fun getSeriesMatchingQuery(query: String): PagingSource<Int, Series>

    fun getHiddenStateOfAllSeries(): Flow<List<SeriesHiddenState>>

    suspend fun getIdAndNameOfAllSeries(): List<SeriesIdAndName>

}