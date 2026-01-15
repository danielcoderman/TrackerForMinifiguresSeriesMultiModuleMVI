package com.drsapps.trackerforlegominifiguresseries.domain.repository

import androidx.paging.PagingSource
import com.drsapps.trackerforlegominifiguresseries.domain.model.Minifigure
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureHiddenState
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureWithSeriesName
import kotlinx.coroutines.flow.Flow

interface MinifigureRepository {

    fun getVisibleMinifigures(): Flow<List<Minifigure>>

    fun getFavoriteMinifigures(): Flow<List<Minifigure>>

    fun getCollectedMinifigures(): Flow<List<Minifigure>>

    fun getWishlistedMinifigures(): Flow<List<Minifigure>>

    fun getUncollectedMinifigures(): Flow<List<Minifigure>>

    fun getMinifigure(minifigureId: Int): Flow<MinifigureWithSeriesName>

    fun getAllMinifiguresFromSeries(seriesId: Int): Flow<List<MinifigureHiddenState>>

    fun getVisibleMinifiguresFromSeries(seriesId: Int): Flow<List<Minifigure>>

    suspend fun toggleMinifigureCollectedStateAndResetWishlistedState(minifigureId: Int)

    suspend fun toggleMinifigureWishlistedStateAndResetCollectedState(minifigureId: Int)

    suspend fun toggleMinifigureFavoriteState(minifigureId: Int)

    suspend fun toggleMinifigureHiddenState(minifigureId: Int)

    fun getMinifiguresMatchingQuery(query: String): PagingSource<Int, MinifigureWithSeriesName>

    suspend fun hideSeries(seriesId: Int)

    suspend fun unhideSeries(seriesId: Int)

}