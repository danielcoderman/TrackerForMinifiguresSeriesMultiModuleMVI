package com.drsapps.trackerforlegominifiguresseries.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import coil.compose.AsyncImage
import com.drsapps.trackerforlegominifiguresseries.R
import com.drsapps.trackerforlegominifiguresseries.domain.model.Series
import com.drsapps.trackerforlegominifiguresseries.presentation.util.SeriesFilter
import com.drsapps.trackerforlegominifiguresseries.ui.theme.DarkTextColor
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme
import com.drsapps.trackerforlegominifiguresseries.ui.theme.White

@Composable
fun SeriesCard(
    series: Series,
    onSeriesImageClick: (id: Int) -> Unit,
    onSeriesClick: (id: Int, title: String) -> Unit,
    filter: SeriesFilter? = null
) {

    // Used to prevent navigating to multiple screens if their corresponding buttons are
    // clicked on at the same time or about the same time.
    val lifecycleOwner = LocalLifecycleOwner.current

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            val currentLifecycleState = lifecycleOwner.lifecycle.currentState
            if (currentLifecycleState.isAtLeast(Lifecycle.State.STARTED)) { // Note: Original code uses RESUMED, but STARTED works better after two destinations in the navigation bar are clicked on at the same time
                onSeriesClick(series.id, series.name)
            }
        },
        colors = if (filter == SeriesFilter.ALL &&
                    series.numOfMinifigsCollected == series.numOfMinifigs - series.numOfMinifigsHidden
                 ) {
                    CardDefaults.elevatedCardColors(contentColor = DarkTextColor)
                 } else {
                    CardDefaults.elevatedCardColors()
                 }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(1f)
                    .clickable {
                        val currentLifecycleState = lifecycleOwner.lifecycle.currentState
                        if (currentLifecycleState.isAtLeast(Lifecycle.State.STARTED)) { // Note: Original code uses RESUMED, but STARTED works better after two destinations in the navigation bar are clicked on at the same time
                            onSeriesImageClick(series.id)
                        }
                    },
                model = series.imageUrl,
                contentDescription = "${series.name} image",
                error = painterResource(id = R.drawable.image_placeholder),
                contentScale = ContentScale.Fit
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .then(
                        if (filter == SeriesFilter.ALL &&
                            series.numOfMinifigsCollected == series.numOfMinifigs - series.numOfMinifigsHidden
                        ) {
                            Modifier.background(
                                brush = Brush.horizontalGradient(colors = listOf(White, MaterialTheme.colorScheme.primary))
                            )
                        } else {
                            Modifier
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = series.name,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text ="${series.numOfMinifigsCollected}/${series.numOfMinifigs - series.numOfMinifigsHidden}",
                    modifier = Modifier.padding(end = 8.dp),
                    maxLines = 1
                )
            }
        }
    }
}

@Preview
@Composable
fun SeriesCardPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        SeriesCard(
            series = Series(
                id = 1,
                name = "Series 1",
                imageUrl = "",
                numOfMinifigs = 16,
                releaseDate = "2010-06-01",
                favorite = false,
                numOfMinifigsCollected = 16,
                numOfMinifigsHidden = 0
            ),
            onSeriesImageClick = {},
            onSeriesClick = { _,_ -> },
            filter = SeriesFilter.ALL
        )
    }
}