package com.drsapps.trackerforlegominifiguresseries.util

sealed class SyncState {
    data object Loading : SyncState()
    data object Success : SyncState()
    data object NoNewData : SyncState()
    data class Failure(val message: String) : SyncState()
}