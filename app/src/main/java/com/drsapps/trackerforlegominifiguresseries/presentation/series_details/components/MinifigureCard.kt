package com.drsapps.trackerforlegominifiguresseries.presentation.series_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import coil.compose.AsyncImage
import com.drsapps.trackerforlegominifiguresseries.R
import com.drsapps.trackerforlegominifiguresseries.domain.model.Minifigure
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme

@Composable
fun MinifigureCard(
    minifigure: Minifigure,
    onClick: (id: Int, title: String) -> Unit,
    toggleMinifigureCollectedStateAndResetWishlistedState: (Int) -> Unit,
    toggleMinifigureWishlistedStateAndResetCollectedState: (Int) -> Unit,
    toggleMinifigureFavoriteState: (Int) -> Unit
) {

    // Used to prevent navigating to multiple minifigure details screens if minifigure cards are
    // clicked on at the same time or about the same time.
    val lifecycleOwner = LocalLifecycleOwner.current

    // Used to get the height of the screen in dp
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp
    val topAppBarHeight = 64.dp
    val approximateHeightOfBottomPartOfCard = 100.dp // adding a bit of extra dp to the height (84.dp is the actual height)
    val approximateHeightOfBannerAd = 48.dp
    val minifigureImageWidth = screenHeight - topAppBarHeight -
            approximateHeightOfBottomPartOfCard - approximateHeightOfBannerAd

    ElevatedCard(
        modifier = Modifier.width(minifigureImageWidth), // Note: Doesn't matter if the width is out of bounds because the incoming constraints prevent that (requiredWidth forces the width)
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
            contentScale = ContentScale.Fit,
        )
        Text(
            text = minifigure.name,
            modifier = Modifier
                .padding(start = 12.dp, top = 12.dp, end = 12.dp)
                // The clearAndSetSemantics function is used so that the Accessibility Service doesn't interact with the Text but instead
                // with the minifigure image above. After all, the minifigure image appears first when scrolling through a list.
                .clearAndSetSemantics {  },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    toggleMinifigureCollectedStateAndResetWishlistedState(minifigure.id)
                }
            ) {
                if (minifigure.collected) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filled_collected),
                        contentDescription = stringResource(R.string.remove_from_collected, minifigure.name),
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_outlined_collected),
                        contentDescription = stringResource(R.string.add_to_collected, minifigure.name)
                    )
                }
            }
            IconButton(
                onClick = {
                    toggleMinifigureWishlistedStateAndResetCollectedState(minifigure.id)
                }
            ) {
                if (minifigure.wishListed) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = stringResource(id = R.string.remove_from_wishlist, minifigure.name),
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(id = R.string.add_to_wishlist, minifigure.name)
                    )
                }
            }
            IconButton(
                onClick = {
                    toggleMinifigureFavoriteState(minifigure.id)
                }
            ) {
                if (minifigure.favorite) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filled_favorite),
                        contentDescription = stringResource(id = R.string.remove_from_favorites, minifigure.name),
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_outlined_favorite),
                        contentDescription = stringResource(id = R.string.add_to_favorites, minifigure.name)
                    )
                }
            }
        }
    }

}

@Preview
@Composable
fun MinifigureCardPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        MinifigureCard(
            minifigure = Minifigure(
                id = 1,
                name = "Minifig 1",
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
            toggleMinifigureCollectedStateAndResetWishlistedState = {},
            toggleMinifigureWishlistedStateAndResetCollectedState = {},
            toggleMinifigureFavoriteState = {}
        )
    }
}