package com.drsapps.trackerforlegominifiguresseries.presentation.minifigure_details.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.drsapps.trackerforlegominifiguresseries.R
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinifigureDetailsTopAppBar(
    title: String,
    isCollected: Boolean?,
    isWishlisted: Boolean?,
    isFavorite: Boolean?,
    onToggleCollectedState: () -> Unit,
    onToggleWishlistedState: () -> Unit,
    onToggleFavoriteState: () -> Unit,
    onNavigateBack: () -> Unit
) {

    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.navigate_back)
                )
            }
        },
        actions = {
            IconButton(onClick = onToggleCollectedState) {
                isCollected?.let {
                    if (it) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filled_collected),
                            contentDescription = stringResource(id = R.string.remove_from_collected, "")
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_outlined_collected),
                            contentDescription = stringResource(id = R.string.add_to_collected, "")
                        )
                    }
                }
            }
            IconButton(onClick = onToggleWishlistedState) {
                isWishlisted?.let {
                    if (it) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = stringResource(id = R.string.remove_from_wishlist, "")
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(id = R.string.add_to_wishlist, "")
                        )
                    }
                }
            }
            IconButton(onClick = onToggleFavoriteState) {
                isFavorite?.let {
                    if (it) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filled_favorite),
                            contentDescription = stringResource(id = R.string.remove_from_favorites, "")
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_outlined_favorite),
                            contentDescription = stringResource(id = R.string.add_to_favorites, "")
                        )
                    }
                }
            }
        }
    )

}

@Preview
@Composable
fun MinifigureDetailsTopAppBarPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        MinifigureDetailsTopAppBar(
            title = "Series 100",
            isCollected = false,
            isWishlisted = true,
            isFavorite = false,
            onToggleCollectedState = {},
            onToggleFavoriteState = {},
            onToggleWishlistedState = {},
            onNavigateBack = {}
        )
    }
}