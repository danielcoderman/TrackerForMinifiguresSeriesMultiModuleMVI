package com.drsapps.trackerforlegominifiguresseries.presentation.series_image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.drsapps.trackerforlegominifiguresseries.R
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme

@Composable
fun SeriesImageScreen(
    viewModel: SeriesImageViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {

    val seriesImageUrl by viewModel.seriesImageUrl.collectAsStateWithLifecycle()

    SeriesImageScreen(
        seriesImageUrl = seriesImageUrl,
        onNavigateBack = onNavigateBack
    )
    
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesImageScreen(
    seriesImageUrl: String,
    onNavigateBack: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
                model = seriesImageUrl,
                contentDescription = null,
                error = painterResource(id = R.drawable.image_placeholder),
                contentScale = ContentScale.Fit
            )
        }

    }

}

@Preview
@Composable
fun SeriesImageScreenPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        SeriesImageScreen(
            seriesImageUrl = "",
            onNavigateBack = {}
        )
    }
}