package com.drsapps.trackerforlegominifiguresseries.presentation.series_details.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Layout
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesDetailsTopAppBar(
    title: String,
    isSeriesFavorite: Boolean?,
    onToggleSeriesFavoriteState: () -> Unit,
    layout: Layout?,
    onToggleLayout: () -> Unit,
    onNavigateBack: () -> Unit,
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
            IconButton(onClick = onToggleSeriesFavoriteState) {
                isSeriesFavorite?.let {
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
            IconButton(onClick = onToggleLayout) {
                layout?.let {
                    if (it == Layout.GRID) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filled_list_view),
                            contentDescription = stringResource(id = R.string.switch_to_list_view)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filled_grid_view),
                            contentDescription = stringResource(id = R.string.switch_to_grid_view)
                        )
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun SeriesDetailsTopAppBarPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        SeriesDetailsTopAppBar(
            title = "Series 100",
            isSeriesFavorite = false,
            onToggleSeriesFavoriteState = {},
            layout = Layout.GRID,
            onToggleLayout = {},
            onNavigateBack = {}
        )
    }
}