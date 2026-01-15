package com.drsapps.trackerforlegominifiguresseries.domain.use_case

import com.drsapps.trackerforlegominifiguresseries.domain.repository.AppStateRepository
import com.drsapps.trackerforlegominifiguresseries.domain.repository.SyncRepository
import com.drsapps.trackerforlegominifiguresseries.util.SyncState

class SyncDataUseCase(
    private val syncRepo: SyncRepository,
    private val appStateRepo: AppStateRepository
) {
    suspend operator fun invoke(): SyncState {
        val appState = appStateRepo.getAppState()
        val syncResult = syncRepo.syncData(
            lastFetchedTimestamp = appState.lastFetchedTimestamp,
            updateLastFetchedTimestamp = appStateRepo::updateLastFetchedTimestamp,
            getAppState = appStateRepo::getAppState
        )
        return syncResult
    }
}
