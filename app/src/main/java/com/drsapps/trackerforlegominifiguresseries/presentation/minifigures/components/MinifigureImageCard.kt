package com.drsapps.trackerforlegominifiguresseries.presentation.minifigures.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import coil.compose.AsyncImage
import com.drsapps.trackerforlegominifiguresseries.R
import com.drsapps.trackerforlegominifiguresseries.domain.model.Minifigure
import com.drsapps.trackerforlegominifiguresseries.presentation.util.MinifiguresFilter
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme

@Composable
fun MinifigureImageCard(
    minifigure: Minifigure,
    onClick: (id: Int, title: String) -> Unit,
    filter: MinifiguresFilter
) {

    // Used to prevent navigating to many minifigure details screens if minifigure image cards are
    // clicked on at the same time or about the same time.
    val lifecycleOwner = LocalLifecycleOwner.current

    ElevatedCard(
        modifier = Modifier.then(
            if (filter == MinifiguresFilter.ALL && minifigure.collected) {
                Modifier.border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
            } else {
                Modifier
            }
        ),
        onClick = {
            val currentLifecycleState = lifecycleOwner.lifecycle.currentState
            if (currentLifecycleState.isAtLeast(Lifecycle.State.RESUMED)) {
                onClick(minifigure.id, minifigure.name)
            }
        }
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            model = minifigure.imageUrl,
            contentDescription = minifigure.name,
            error = painterResource(id = R.drawable.image_placeholder),
            contentScale = ContentScale.Fit
        )
    }

}

@Preview
@Composable
fun MinifigureImageCardPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        MinifigureImageCard(
            minifigure = Minifigure(
                id = 1,
                name = "Minifigure 1",
                imageUrl = "",
                positionInSeries = 1,
                seriesId = 1,
                collected = true,
                wishListed = false,
                favorite = false,
                hidden = false,
                numOfCollectedInventoryItems = 5,
                inventorySize = 5
            ),
            onClick = { _,_ -> },
            filter = MinifiguresFilter.ALL
        )
    }
}