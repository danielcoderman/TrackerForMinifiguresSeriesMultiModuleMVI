package com.drsapps.trackerforlegominifiguresseries.presentation.util

import androidx.annotation.DrawableRes
import com.drsapps.trackerforlegominifiguresseries.R

enum class Screen(
    val route: String,
    val title: String? = null,
    @DrawableRes val selectedIconResourceId: Int? = null,
    @DrawableRes val unselectedIconResourceId: Int? = null
) {
    SplashScreen("splash_screen"),
    Series("series", "Series", R.drawable.ic_filled_series_screen, R.drawable.ic_outlined_series_screen),
    Minifigures("minifigs", "Minifigures", R.drawable.ic_filled_minifigures_screen, R.drawable.ic_outlined_minifigures_screen),
//    Settings("settings", "Settings", R.drawable.ic_filled_settings_screen, R.drawable.ic_outlined_settings_screen),
    SeriesDetails("series_details"),
    MinifigureDetails("minifigure_details"),
    SeriesSearch("series_search"),
    HideSeries("hide_series", "Hide Series"),
    HideMinifigures("hide_minifigures", "Hide Minifigures"),
    SearchMinifigures("search_minifigures"),
    SeriesImage("series_image")
}

val navBarScreens = listOf(
    Screen.Series,
    Screen.Minifigures,
//    Screen.Settings
)

val navRailScreens = navBarScreens
