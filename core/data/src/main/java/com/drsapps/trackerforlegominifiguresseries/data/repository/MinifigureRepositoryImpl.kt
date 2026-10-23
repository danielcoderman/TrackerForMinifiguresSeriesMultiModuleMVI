package com.drsapps.trackerforlegominifiguresseries.data.repository

import android.util.Log
import androidx.paging.PagingSource
import com.drsapps.trackerforlegominifiguresseries.data.local.MinifigureDao
import com.drsapps.trackerforlegominifiguresseries.domain.model.Minifigure
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureHiddenState
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureWithSeriesName
import com.drsapps.trackerforlegominifiguresseries.domain.repository.MinifigureRepository
import kotlinx.coroutines.flow.Flow

private const val TAG = "MinifigureRepositoryImpl"

class MinifigureRepositoryImpl(
    private val dao: MinifigureDao
) : MinifigureRepository {

    override fun getVisibleMinifigures(): Flow<List<Minifigure>> {
        return dao.getVisibleMinifigures()
    }

    override fun getFavoriteMinifigures(): Flow<List<Minifigure>> {
        return dao.getFavoriteMinifigures()
    }

    override fun getCollectedMinifigures(): Flow<List<Minifigure>> {
        return dao.getCollectedMinifigures()
    }

    override fun getWishlistedMinifigures(): Flow<List<Minifigure>> {
        return dao.getWishlistedMinifigures()
    }

    override fun getUncollectedMinifigures(): Flow<List<Minifigure>> {
        return dao.getUncollectedMinifigures()
    }

    override fun getMinifigure(minifigureId: Int): Flow<MinifigureWithSeriesName> {
        return dao.getMinifigure(minifigureId)
    }

    override fun getAllMinifiguresFromSeries(seriesId: Int): Flow<List<MinifigureHiddenState>> {
        return dao.getAllMinifiguresFromSeries(seriesId)
    }

    override fun getVisibleMinifiguresFromSeries(seriesId: Int): Flow<List<Minifigure>> {
        return dao.getVisibleMinifiguresFromSeries(seriesId)
    }

    override suspend fun toggleMinifigureCollectedStateAndResetWishlistedState(minifigureId: Int) {
        val numOfRowsUpdated = dao.toggleMinifigureCollectedStateAndResetWishlistedState(minifigureId)
        if (numOfRowsUpdated == 0) {
            Log.e(TAG, "toggleMinifigureCollectedStateAndResetWishlistedState: No update occurred")
        }
    }

    override suspend fun toggleMinifigureWishlistedStateAndResetCollectedState(minifigureId: Int) {
        val numOfRowsUpdated = dao.toggleMinifigureWishlistedStateAndResetCollectedState(minifigureId)
        if (numOfRowsUpdated == 0) {
            Log.e(TAG, "toggleMinifigureWishlistedStateAndResetCollectedState: No update occurred")
        }
    }

    override suspend fun toggleMinifigureFavoriteState(minifigureId: Int) {
        val numOfRowsUpdated = dao.toggleMinifigureFavoriteState(minifigureId)
        if (numOfRowsUpdated == 0) {
            Log.e(TAG, "toggleMinifigureFavoriteState: No update occurred")
        }
    }

    override suspend fun toggleMinifigureHiddenState(minifigureId: Int) {
        val numOfRowsUpdated = dao.toggleMinifigureHiddenState(minifigureId)
        if (numOfRowsUpdated == 0) {
            Log.e(TAG, "toggleMinifigureHiddenState: No update occurred")
        }
    }

    override fun getMinifiguresMatchingQuery(query: String): PagingSource<Int, MinifigureWithSeriesName> {
        return dao.getMinifiguresMatchingQuery(query)
    }

    override suspend fun hideSeries(seriesId: Int) {
        val numOfRowsUpdated = dao.hideSeries(seriesId)
        if (numOfRowsUpdated == 0) {
            Log.e(TAG, "hideSeries: Operation failed")
        }
    }

    override suspend fun unhideSeries(seriesId: Int) {
        val numOfRowsUpdated = dao.unhideSeries(seriesId)
        if (numOfRowsUpdated == 0) {
            Log.e(TAG, "unhideSeries: Operation failed")
        }
    }

}