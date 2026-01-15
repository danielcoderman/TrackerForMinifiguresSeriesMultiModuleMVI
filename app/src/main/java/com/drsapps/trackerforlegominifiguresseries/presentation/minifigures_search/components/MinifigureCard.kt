package com.drsapps.trackerforlegominifiguresseries.presentation.minifigures_search.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import coil.compose.AsyncImage
import com.drsapps.trackerforlegominifiguresseries.R
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureWithSeriesName
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme

@Composable
fun MinifigureCard(
    minifigure: MinifigureWithSeriesName,
    onClick: (id: Int, title: String) -> Unit,
) {

    // Used to prevent navigating to multiple minifigure details screens if multiple minifigure
    // cards are clicked on at the same time or about the same time.
    val lifecycleOwner = LocalLifecycleOwner.current

    ElevatedCard(
        onClick = {
            val currentLifecycleState = lifecycleOwner.lifecycle.currentState
            if (currentLifecycleState.isAtLeast(Lifecycle.State.RESUMED)) {
                onClick(minifigure.id, minifigure.name)
            }
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(1f),
                model = minifigure.imageUrl,
                contentDescription = null, // description not needed because of the text below
                error = painterResource(id = R.drawable.image_placeholder),
                contentScale = ContentScale.Fit
            )
            Column(
                modifier = Modifier.padding(start = 16.dp, end = 8.dp)
            ) {
                Text(
                    text = minifigure.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = minifigure.seriesName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

}

@Preview
@Composable
fun MinifigureCardPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        MinifigureCard(
            minifigure = MinifigureWithSeriesName(
                id = 1,
                name = "Minifig 1",
                imageUrl = "",
                favorite = false,
                collected = true,
                wishListed = false,
                seriesName = "Series 1"
            ),
            onClick = { _,_ -> }
        )
    }
}