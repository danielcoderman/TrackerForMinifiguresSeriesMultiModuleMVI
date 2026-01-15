package com.drsapps.trackerforlegominifiguresseries.presentation.components

import android.util.Log
import android.view.ViewTreeObserver
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.drsapps.trackerforlegominifiguresseries.R
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme
import kotlinx.coroutines.launch

@Composable
fun SearchBar(
    isKeyboardOpenOnSearchScreenAppear: Boolean,
    onKeyboardVisibilityChange: (Boolean) -> Unit,
    placeholderText: String,
    searchTextFieldValue: TextFieldValue,
    changeSearchTextFieldValueAndPagingData: (TextFieldValue) -> Unit,
    resetSearchResultListScrollingPosition: suspend () -> Unit,
    isClearButtonVisible: Boolean,
    onClearText: () -> Unit,
    onNavigateBack: () -> Unit
) {

    // Creates a CoroutineScope bound to the SearchBar's lifecycle
    val coroutineScope = rememberCoroutineScope()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // The code below listens to the visibility state of the keyboard. Whenever there's a
            // change in the keyboard visibility the viewModel of the corresponding search screen
            // gets the updated value.
            val view = LocalView.current
            val viewTreeObserver = view.viewTreeObserver
            DisposableEffect(viewTreeObserver) {
                val listener = ViewTreeObserver.OnGlobalLayoutListener {
                    val isKeyBoardOpen = ViewCompat.getRootWindowInsets(view)
                        ?.isVisible(WindowInsetsCompat.Type.ime()) ?: true

                    if (isKeyBoardOpen) {
                        Log.d("SearchBar", "Keyboard is open")
                        onKeyboardVisibilityChange(true)
                    } else {
                        Log.d("SearchBar", "Keyboard is closed")
                        onKeyboardVisibilityChange(false)
                    }
                }

                viewTreeObserver.addOnGlobalLayoutListener(listener)
                onDispose {
                    if (viewTreeObserver.isAlive) {
                        viewTreeObserver.removeOnGlobalLayoutListener(listener)
                    }
                }
            }

            // Used to programmatically close the keyboard
            val keyBoardController = LocalSoftwareKeyboardController.current

            // Used to give focus to the OutlinedTextField which shows a cursor and opens the keyboard
            val focusRequester = remember { FocusRequester() }

            LaunchedEffect(key1 = Unit) {
                if (isKeyboardOpenOnSearchScreenAppear) {
                    focusRequester.requestFocus()
                }
            }

            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.navigate_back)
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .weight(1F)
                    .focusRequester(focusRequester),
                value = searchTextFieldValue,
                onValueChange = { textFieldValue ->
                    coroutineScope.launch {
                        resetSearchResultListScrollingPosition()
                    }
                    changeSearchTextFieldValueAndPagingData(textFieldValue)
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = isClearButtonVisible,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                resetSearchResultListScrollingPosition()
                            }
                            onClearText()
                        }) {
                            Icon(Icons.Default.Clear, stringResource(id = R.string.clear_search_text))
                        }
                    }
                },
                placeholder = {
                    Text(placeholderText)
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyBoardController?.hide()
                    }
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        HorizontalDivider()
    }

}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        SearchBar(
            isKeyboardOpenOnSearchScreenAppear = true,
            onKeyboardVisibilityChange = {},
            placeholderText = "Search Series",
            searchTextFieldValue = TextFieldValue(""),
            changeSearchTextFieldValueAndPagingData = {},
            resetSearchResultListScrollingPosition = {},
            isClearButtonVisible = false,
            onClearText = {},
            onNavigateBack = {}
        )
    }
}