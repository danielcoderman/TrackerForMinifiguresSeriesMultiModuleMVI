package com.drsapps.trackerforlegominifiguresseries.feature.series.util

// Duplicates the literal values of the matching keys in :app's own Constants.kt. :app's AppNavHost
// defines these as navArgument names, and this feature's ViewModels read the same keys back out of
// the SavedStateHandle Hilt injects - but now that :app depends on this module (not the reverse),
// those ViewModels can no longer import :app's Constants directly. The real fix is each feature
// owning its own navigation destination/arg definitions (Now in Android style) with :app's NavHost
// just composing them - deferred as a design shortcut for the eventual navigation pass, not fixed
// here, since the string values must stay in sync with :app's Constants.kt by convention until then.
object Constants {
    const val SERIES_ID = "seriesId"

    const val FILTER = "filter"
    const val SEARCH_TEXT_FIELD_VALUE = "searchTextFieldValue"
    const val IS_CLEAR_BUTTON_VISIBLE = "isClearButtonVisible"
    const val IS_KEYBOARD_OPEN_ON_SCREEN_APPEAR = "isKeyboardOpenOnScreenAppear"
}
