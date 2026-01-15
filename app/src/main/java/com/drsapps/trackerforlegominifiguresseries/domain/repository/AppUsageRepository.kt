package com.drsapps.trackerforlegominifiguresseries.domain.repository

import com.drsapps.trackerforlegominifiguresseries.domain.model.AppUsage
import kotlinx.coroutines.flow.Flow

interface AppUsageRepository {
    fun getAppUsage(): Flow<AppUsage>

    suspend fun incrementAppOpenCount()

    suspend fun incrementSignificantActionsCount()
}