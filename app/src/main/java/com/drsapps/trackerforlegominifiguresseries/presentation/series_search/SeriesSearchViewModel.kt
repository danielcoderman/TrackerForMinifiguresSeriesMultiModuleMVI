package com.drsapps.trackerforlegominifiguresseries.presentation.series_search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.drsapps.trackerforlegominifiguresseries.domain.model.Series
import com.drsapps.trackerforlegominifiguresseries.domain.repository.SeriesRepository
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Constants.IS_CLEAR_BUTTON_VISIBLE
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Constants.IS_KEYBOARD_OPEN_ON_SCREEN_APPEAR
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Constants.SEARCH_TEXT_FIELD_VALUE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeriesSearchViewModel @Inject constructor (
    private val savedStateHandle: SavedStateHandle,
    private val repo: SeriesRepository
) : ViewModel() {

    var isKeyboardOpenOnScreenAppear = savedStateHandle[IS_KEYBOARD_OPEN_ON_SCREEN_APPEAR] ?: true
        private set

    // I'm using mutableStateOf() instead of MutableStateFlow() because it allows saving a
    // TextFieldValue() in savedStateHandle with the help of saveable().
    @OptIn(SavedStateHandleSaveableApi::class)
    var searchTextFieldValue by savedStateHandle.saveable(
        key = SEARCH_TEXT_FIELD_VALUE,
        stateSaver = TextFieldValue.Saver
    ) {
        mutableStateOf(TextFieldValue(""))
    }
        private set

    val isClearButtonVisible = savedStateHandle.getStateFlow(IS_CLEAR_BUTTON_VISIBLE, false)

    private val _seriesPagingData = MutableStateFlow<PagingData<Series>>(PagingData.empty())
    val seriesPagingData = _seriesPagingData.asStateFlow()

    private val pagingConfig = PagingConfig(
        pageSize = 20,
        prefetchDistance = 20,
        initialLoadSize = 40,
    )

    private var pager: Pager<Int, Series>? = null

    private var getSeriesPagingDataJob: Job? = null

    init {
        // On process recreation, due to system-initiated process death, get the series paging data
        // corresponding to the restored search text field value.
        if (searchTextFieldValue.text.isNotEmpty()) {
            createPagerAndStartJobToGetSeriesPagingData(searchTextFieldValue.text.trimEnd())
        }
    }

    fun changeIsKeyboardOpenOnScreenAppear(value: Boolean) {
        isKeyboardOpenOnScreenAppear = value
        savedStateHandle[IS_KEYBOARD_OPEN_ON_SCREEN_APPEAR] = value
    }

    fun changeSearchTextFieldValueAndSeriesPagingData(textFieldValue: TextFieldValue) {
        searchTextFieldValue = textFieldValue
        savedStateHandle[IS_CLEAR_BUTTON_VISIBLE] = searchTextFieldValue.text.isNotEmpty()
        if (searchTextFieldValue.text.isNotEmpty()) {
            // Any trailing whitespace after the search text is removed when searching for matching
            // minifigures in the database. This is done because selecting a suggested word from the
            // Android keyboard adds a space after the word.
            createPagerAndStartJobToGetSeriesPagingData(searchTextFieldValue.text.trimEnd())
        } else {
            getSeriesPagingDataJob?.cancel()
            pager = null
            _seriesPagingData.value = PagingData.empty()
        }
    }

    fun clearText() {
        changeSearchTextFieldValueAndSeriesPagingData(TextFieldValue(""))
    }

    private fun createPagerAndStartJobToGetSeriesPagingData(query: String) {
        getSeriesPagingDataJob?.cancel()

        pager = Pager(
            config = pagingConfig,
            pagingSourceFactory = { repo.getSeriesMatchingQuery(query) }
        )

        getSeriesPagingDataJob = viewModelScope.launch {
            pager!!.flow.cachedIn(viewModelScope).flowOn(Dispatchers.IO).collect { seriesPagingData ->
                _seriesPagingData.value = seriesPagingData
            }
        }
    }

}