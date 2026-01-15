package com.drsapps.trackerforlegominifiguresseries.presentation.series_search

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.drsapps.trackerforlegominifiguresseries.domain.model.Series
import com.drsapps.trackerforlegominifiguresseries.presentation.components.SearchBar
import com.drsapps.trackerforlegominifiguresseries.presentation.components.SeriesCard
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "SeriesSearchScreen"

@Composable
fun SeriesSearchScreen(
    viewModel: SeriesSearchViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onSeriesImageClick: (id: Int) -> Unit,
    onSeriesClick: (id: Int, title: String) -> Unit
) {

    val searchTextFieldValue = viewModel.searchTextFieldValue
    val isClearButtonVisible by viewModel.isClearButtonVisible.collectAsStateWithLifecycle()

    SeriesSearchScreen(
        isKeyboardOpenOnScreenAppear = viewModel.isKeyboardOpenOnScreenAppear,
        onKeyboardVisibilityChange = viewModel::changeIsKeyboardOpenOnScreenAppear,
        searchTextFieldValue = searchTextFieldValue,
        changeSearchTextFieldValueAndSeriesPagingData = viewModel::changeSearchTextFieldValueAndSeriesPagingData,
        seriesPagingData = viewModel.seriesPagingData,
        isClearButtonVisible = isClearButtonVisible,
        onClearText = viewModel::clearText,
        onNavigateBack = onNavigateBack,
        onSeriesImageClick = onSeriesImageClick,
        onSeriesClick = onSeriesClick
    )

}

@Composable
fun SeriesSearchScreen(
    isKeyboardOpenOnScreenAppear: Boolean,
    onKeyboardVisibilityChange: (Boolean) -> Unit,
    searchTextFieldValue: TextFieldValue,
    changeSearchTextFieldValueAndSeriesPagingData: (TextFieldValue) -> Unit,
    seriesPagingData: StateFlow<PagingData<Series>>,
    isClearButtonVisible: Boolean,
    onClearText: () -> Unit,
    onNavigateBack: () -> Unit,
    onSeriesImageClick: (id: Int) -> Unit,
    onSeriesClick: (id: Int, title: String) -> Unit
) {

    // Used to reset the scrolling position of the search result list/grid
    val lazyGridState = rememberLazyGridState()

    Scaffold(
        topBar = {
            SearchBar(
                isKeyboardOpenOnSearchScreenAppear = isKeyboardOpenOnScreenAppear,
                onKeyboardVisibilityChange = onKeyboardVisibilityChange,
                placeholderText = "Search series",
                searchTextFieldValue = searchTextFieldValue,
                changeSearchTextFieldValueAndPagingData = changeSearchTextFieldValueAndSeriesPagingData,
                resetSearchResultListScrollingPosition = {
                    lazyGridState.scrollToItem(0)
                },
                isClearButtonVisible = isClearButtonVisible,
                onClearText = onClearText,
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->

        val lazyPagingSeries = seriesPagingData.collectAsLazyPagingItems()
        // This condition checks if there are no search results after the initial load of the paging
        // data has been taken into account.
        if (lazyPagingSeries.itemCount == 0 && searchTextFieldValue.text.isNotEmpty() &&
            lazyPagingSeries.loadState.refresh is LoadState.NotLoading) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(start = 16.dp, top = 16.dp)
                    .consumeWindowInsets(paddingValues)
            ) {
                Text("No series found matching your query")
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
                    .imePadding() // [1]
                ,
                columns = GridCells.Adaptive(315.dp),
                state = lazyGridState,
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(count = lazyPagingSeries.itemCount) { index ->
                    val series = lazyPagingSeries[index]

                    if (series == null) {
                        Log.e(TAG, "Series is null")
                        Log.d(TAG, "Index: $index\n" +
                                "Search text: ${searchTextFieldValue.text}\n" +
                                "Item count: ${lazyPagingSeries.itemCount}"
                        )
                    } else {
                        SeriesCard(
                            series = series,
                            onSeriesImageClick = onSeriesImageClick,
                            onSeriesClick = onSeriesClick
                        )
                    }
                }
            }
        }
    }

}

// The following link shows how to preview composables that display paging items:
// https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:paging/paging-compose/samples/src/main/java/androidx/paging/compose/samples/PagingPreviewSample.kt;l=37?q=Pagingpreviewsample
@Preview
@Composable
fun SeriesSearchScreenPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        val series = listOf(
            Series(
                id = 1,
                name = "Series 1",
                imageUrl = "",
                numOfMinifigs = 12,
                releaseDate = "",
                favorite = false,
                numOfMinifigsCollected = 1,
                numOfMinifigsHidden = 0
            ),
            Series(
                id = 2,
                name = "Series 2",
                imageUrl = "",
                numOfMinifigs = 12,
                releaseDate = "",
                favorite = false,
                numOfMinifigsCollected = 5,
                numOfMinifigsHidden = 1
            ),
            Series(
                id = 3,
                name = "Series 3",
                imageUrl = "",
                numOfMinifigs = 12,
                releaseDate = "",
                favorite = false,
                numOfMinifigsCollected = 0,
                numOfMinifigsHidden = 0
            )
        )
        val seriesPagingData = PagingData.from(series)
        val seriesPagingDataFlow = MutableStateFlow(seriesPagingData).asStateFlow()

        SeriesSearchScreen(
            isKeyboardOpenOnScreenAppear = false,
            onKeyboardVisibilityChange = {},
            searchTextFieldValue = TextFieldValue("Ser"),
            changeSearchTextFieldValueAndSeriesPagingData = {},
            seriesPagingData = seriesPagingDataFlow,
            isClearButtonVisible = true,
            onClearText = {},
            onNavigateBack = {},
            onSeriesImageClick = {},
            onSeriesClick = { _,_ -> }
        )
    }
}

// Notes
// [1]: This basically lets the LazyVerticalGrid know when to size up or down depending on whether
//      the soft keyboard is open or not. A nice and simple animation effect is provided by default.