package com.drsapps.trackerforlegominifiguresseries.presentation.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.drsapps.trackerforlegominifiguresseries.R
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesAndMinifiguresTopAppBar(
    title: String,
    onSearchClicked: () -> Unit,
    onVisibilityClicked: () -> Unit,
    @StringRes searchIconDescResId: Int,
    @StringRes visibilityIconDescResId: Int,
    scrollBehavior: TopAppBarScrollBehavior
) {

    // Used to prevent navigating to multiple screens if their corresponding buttons are
    // clicked on at the same time or about the same time.
    val lifecycleOwner = LocalLifecycleOwner.current

    TopAppBar(
        title = {
            Text(
                text = title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        actions = {
            IconButton(onClick = {
                val currentLifecycleState = lifecycleOwner.lifecycle.currentState
                if (currentLifecycleState.isAtLeast(Lifecycle.State.STARTED)) {
                    onSearchClicked()
                }
            }) {
                Icon(Icons.Default.Search, stringResource(searchIconDescResId))
            }
            IconButton(onClick = {
                val currentLifecycleState = lifecycleOwner.lifecycle.currentState
                if (currentLifecycleState.isAtLeast(Lifecycle.State.STARTED)) {
                    onVisibilityClicked()
                }
            }) {
                Icon(painterResource(id = R.drawable.ic_filled_visibility), stringResource(id = visibilityIconDescResId))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        scrollBehavior = scrollBehavior
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SeriesAndMinifiguresTopAppBarPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        SeriesAndMinifiguresTopAppBar(
            title = "Minifigures",
            onSearchClicked = {},
            onVisibilityClicked = {},
            searchIconDescResId = R.string.search_minifigures,
            visibilityIconDescResId = R.string.hide_minifigures,
            scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        )
    }
}