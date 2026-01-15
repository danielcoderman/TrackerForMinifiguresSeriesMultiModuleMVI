package com.drsapps.trackerforlegominifiguresseries.presentation.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme
import com.drsapps.trackerforlegominifiguresseries.util.SyncState

@Composable
fun SplashScreen(
    onDataSynced: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val syncDataState by viewModel.syncDataState.collectAsStateWithLifecycle()

    SplashScreen(
        syncDataState = syncDataState,
        onDataSynced = onDataSynced,
        onRetryClick = viewModel::retry
    )
}

@Composable
fun SplashScreen(
    syncDataState: SyncState,
    onDataSynced: () -> Unit,
    onRetryClick: () -> Unit
) {
    Box (
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (syncDataState) {
                is SyncState.Loading -> {
                    CircularProgressIndicator()

                    Text(
                        text = "Syncing series and minifigure data...",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
                is SyncState.Success -> {
                    LaunchedEffect(Unit) {
                        onDataSynced()
                    }
                }
                is SyncState.Failure -> {
                    Text(
                        text = syncDataState.message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )

                    Button(
                        onClick = onRetryClick
                    ) {
                        Text("Retry")
                    }
                }
                is SyncState.NoNewData -> {
                    // This case shouldn't happen here in the splash screen, but it could happen if
                    // the REST API returns no data, which is unexpected. The first data sync for
                    // all users should fetch all data from the remote database.
                    Text(
                        text = "Couldn't fetch any series and minifigure data. The issue is on our side.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = onRetryClick
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        SplashScreen(
            syncDataState = SyncState.Loading,
            onDataSynced = {},
            onRetryClick = {}
        )
    }
}
