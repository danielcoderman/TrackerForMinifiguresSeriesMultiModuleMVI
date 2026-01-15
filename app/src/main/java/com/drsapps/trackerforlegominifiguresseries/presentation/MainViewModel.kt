package com.drsapps.trackerforlegominifiguresseries.presentation

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drsapps.trackerforlegominifiguresseries.data.worker.DataSyncScheduler
import com.drsapps.trackerforlegominifiguresseries.domain.repository.AppStateRepository
import com.drsapps.trackerforlegominifiguresseries.domain.repository.AppUsageRepository
import com.drsapps.trackerforlegominifiguresseries.domain.repository.SeriesRepository
import com.drsapps.trackerforlegominifiguresseries.presentation.util.hasDaysPassedSince
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainViewModelReviewDelegate: MainViewModelReviewDelegate,
    private val dataSyncScheduler: DataSyncScheduler,
    private val appStateRepo: AppStateRepository,
    private val appUsageRepo: AppUsageRepository,
    private val seriesRepo: SeriesRepository
) : ViewModel() {

    private val _hasExistingData: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val hasExistingData = _hasExistingData.asStateFlow()

    init {
        viewModelScope.launch {
            appUsageRepo.incrementAppOpenCount()
            _hasExistingData.value = isDataExistent()

            // Schedule the data sync if the user has existing data.
            // New users won't have existing data on the first app launch, so once they open the
            // app again with existing data the periodic data sync will be scheduled. It could
            // run right away or at the very end of the 24 hour period.
            if (_hasExistingData.value == true) {
                dataSyncScheduler.schedulePeriodicDataSync()
            }
        }
    }

    private suspend fun isDataExistent(): Boolean {
        val appState = appStateRepo.getAppState()
        if (appState.hasExistingData == null) {
            // If series data exists then minifigure data should exist too.
            // This is because whenever series data is inserted the corresponding minifigure
            // data must be inserted too, or else nothing is inserted.
            val seriesList = seriesRepo.getIdAndNameOfAllSeries()
            val isSeriesListNotEmpty = seriesList.isNotEmpty()
            appStateRepo.updateHasExistingDataValue(isSeriesListNotEmpty)
            return isSeriesListNotEmpty
        } else {
            return appState.hasExistingData
        }
    }

    val shouldShowInAppReview = combine(appStateRepo.getAppStateFlow(), appUsageRepo.getAppUsage()) { appState, appUsage ->
        val minDaysBetweenReviews = 30
        val minAppOpens = 2
        val minSignificantActions = 2
        // TEST
//        Log.d("MainViewModel", "lastReviewRequestTimestamp: ${appState.lastReviewRequestTimestamp}")
//        Log.d("MainViewModel", "numAppOpens: ${appUsage.appOpenCount}")
//        Log.d("MainViewModel", "numSigActions: ${appUsage.significantActionsCount}")
        // TEST

        val hasMinimumDaysPassed = hasDaysPassedSince(appState.lastReviewRequestTimestamp, minDaysBetweenReviews)

        hasMinimumDaysPassed && appUsage.appOpenCount >= minAppOpens &&
                appUsage.significantActionsCount >= minSignificantActions
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)


    fun initializeReviewManager(activity: Activity) {
        mainViewModelReviewDelegate.initializeReviewManager(activity)
    }

    // This function should only be called when shouldShowInAppReview is set to true
    fun requestInAppReviewFlow(activity: Activity) {
        mainViewModelReviewDelegate.requestReviewFlow(
            activity = activity,
            onReviewFlowComplete = {
                viewModelScope.launch {
                    appStateRepo.updateLastReviewRequestTimestamp(Clock.System.now())
                }
            }
        )
    }

}