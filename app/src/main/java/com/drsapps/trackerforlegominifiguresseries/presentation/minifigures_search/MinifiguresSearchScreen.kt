package com.drsapps.trackerforlegominifiguresseries.presentation.minifigures_search

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
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureWithSeriesName
import com.drsapps.trackerforlegominifiguresseries.presentation.components.SearchBar
import com.drsapps.trackerforlegominifiguresseries.presentation.minifigures_search.components.MinifigureCard
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "MinifiguresSearchScreen"

@Composable
fun MinifiguresSearchScreen(
    viewModel: MinifiguresSearchViewModel = hiltViewModel(),
    onMinifigureClick: (id: Int, title: String) -> Unit,
    onNavigateBack: () -> Unit
) {

    val searchTextFieldValue = viewModel.searchTextFieldValue
    val isClearButtonVisible by viewModel.isClearButtonVisible.collectAsStateWithLifecycle()

    MinifiguresSearchScreen(
        isKeyboardOpenOnScreenAppear = viewModel.isKeyboardOpenOnScreenAppear,
        onKeyboardVisibilityChange = viewModel::changeIsKeyboardOpenOnScreenAppear,
        searchTextFieldValue = searchTextFieldValue,
        changeSearchTextFieldValueAndMinifiguresPagingData = viewModel::changeSearchTextFieldValueAndMinifiguresPagingData,
        minifiguresPagingData = viewModel.minifiguresPagingData,
        onMinifigureClick = onMinifigureClick,
        isClearButtonVisible = isClearButtonVisible,
        onClearText = viewModel::clearText,
        onNavigateBack = onNavigateBack,
    )

}

@Composable
fun MinifiguresSearchScreen(
    isKeyboardOpenOnScreenAppear: Boolean,
    onKeyboardVisibilityChange: (Boolean) -> Unit,
    searchTextFieldValue: TextFieldValue,
    changeSearchTextFieldValueAndMinifiguresPagingData: (TextFieldValue) -> Unit,
    minifiguresPagingData: StateFlow<PagingData<MinifigureWithSeriesName>>,
    onMinifigureClick: (id: Int, title: String) -> Unit,
    isClearButtonVisible: Boolean,
    onClearText: () -> Unit,
    onNavigateBack: () -> Unit
) {
    // Used to reset the scrolling position of the search result list/grid
    val lazyGridState = rememberLazyGridState()

    Scaffold(
        topBar = {
            SearchBar(
                isKeyboardOpenOnSearchScreenAppear = isKeyboardOpenOnScreenAppear,
                onKeyboardVisibilityChange = onKeyboardVisibilityChange,
                placeholderText = "Search minifigures",
                searchTextFieldValue = searchTextFieldValue,
                changeSearchTextFieldValueAndPagingData = changeSearchTextFieldValueAndMinifiguresPagingData,
                resetSearchResultListScrollingPosition = {
                    lazyGridState.scrollToItem(0)
                },
                isClearButtonVisible = isClearButtonVisible,
                onClearText = onClearText,
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->

        val lazyPagingMinifigures = minifiguresPagingData.collectAsLazyPagingItems()
        // This condition checks if there are no search results after the initial load of the paging
        // data has been taken into account.
        if (lazyPagingMinifigures.itemCount == 0 && searchTextFieldValue.text.isNotEmpty() &&
            lazyPagingMinifigures.loadState.refresh is LoadState.NotLoading) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(start = 16.dp, top = 16.dp)
                    .consumeWindowInsets(paddingValues)
            ) {
                Text("No minifigures found matching your query")
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
                    .imePadding(), // [1]
                columns = GridCells.Adaptive(330.dp),
                state = lazyGridState,
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(count = lazyPagingMinifigures.itemCount) { index ->
                    val minifigure = lazyPagingMinifigures[index]

                    if (minifigure == null) {
                        Log.e(TAG, "Minifigure is null")
                        Log.d(TAG, "Index: $index\n" +
                                "Search text: ${searchTextFieldValue.text}\n" +
                                "Item count: ${lazyPagingMinifigures.itemCount}"
                        )
                    } else {
                        MinifigureCard(
                            minifigure = minifigure,
                            onClick = onMinifigureClick
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
fun MinifiguresSearchScreenPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        val minifigures = listOf(
            MinifigureWithSeriesName(
                id = 1,
                name = "Minifig 1",
                imageUrl = "",
                collected = true,
                wishListed = false,
                favorite = false,
                seriesName = "Series 1"
            ),
            MinifigureWithSeriesName(
                id = 2,
                name = "Minifig 2",
                imageUrl = "",
                collected = false,
                wishListed = true,
                favorite = true,
                seriesName = "Series 1"
            ),
            MinifigureWithSeriesName(
                id = 3,
                name = "Minifig 3",
                imageUrl = "",
                collected = false,
                wishListed = false,
                favorite = false,
                seriesName = "Series 1"
            )
        )
        val minifiguresPagingData = PagingData.from(minifigures)
        val minifiguresPagingDataFlow = MutableStateFlow(minifiguresPagingData).asStateFlow()
        
        MinifiguresSearchScreen(
            isKeyboardOpenOnScreenAppear = false,
            onKeyboardVisibilityChange = {},
            searchTextFieldValue = TextFieldValue("Min"),
            changeSearchTextFieldValueAndMinifiguresPagingData = {},
            minifiguresPagingData = minifiguresPagingDataFlow,
            onMinifigureClick = { _,_ -> },
            isClearButtonVisible = true,
            onClearText = {},
            onNavigateBack = {}
        )
    }
}

// Notes
// [1]: This basically lets the LazyVerticalGrid know when to size up or down depending on whether
//      the soft keyboard is open or not. A nice and simple animation effect is provided by default.