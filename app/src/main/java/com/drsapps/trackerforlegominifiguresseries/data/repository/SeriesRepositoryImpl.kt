package com.drsapps.trackerforlegominifiguresseries.data.repository

import androidx.paging.PagingSource
import com.drsapps.trackerforlegominifiguresseries.data.local.SeriesDao
import com.drsapps.trackerforlegominifiguresseries.domain.model.Series
import com.drsapps.trackerforlegominifiguresseries.domain.model.SeriesHiddenState
import com.drsapps.trackerforlegominifiguresseries.domain.model.SeriesIdAndName
import com.drsapps.trackerforlegominifiguresseries.domain.repository.SeriesRepository
import kotlinx.coroutines.flow.Flow

class SeriesRepositoryImpl(
    private val dao: SeriesDao
) : SeriesRepository {

    override fun getVisibleSeries(): Flow<List<Series>> {
        return dao.getVisibleSeries()
    }

    override fun getFavoriteSeries(): Flow<List<Series>> {
        return dao.getFavoriteSeries()
    }

    override fun getCompletedSeries(): Flow<List<Series>> {
        return dao.getCompletedSeries()
    }

    override fun getUncompletedSeries(): Flow<List<Series>> {
        return dao.getUncompletedSeries()
    }

    override suspend fun getSeriesImageUrl(seriesId: Int): String {
        return dao.getSeriesImageUrl(seriesId)
    }

    override fun getSeriesFavoriteState(seriesId: Int): Flow<Boolean> {
        return dao.getSeriesFavoriteState(seriesId)
    }

    override suspend fun setSeriesFavoriteState(seriesId: Int, favoriteState: Boolean) {
        dao.setSeriesFavoriteState(seriesId, favoriteState)
    }

    override fun getSeriesMatchingQuery(query: String): PagingSource<Int, Series> {
        return dao.getSeriesMatchingQuery(query)
    }

    override fun getHiddenStateOfAllSeries(): Flow<List<SeriesHiddenState>> {
        return dao.getHiddenStateOfAllSeries()
    }

    override suspend fun getIdAndNameOfAllSeries(): List<SeriesIdAndName> {
        return dao.getIdAndNameOfAllSeries()
    }
}