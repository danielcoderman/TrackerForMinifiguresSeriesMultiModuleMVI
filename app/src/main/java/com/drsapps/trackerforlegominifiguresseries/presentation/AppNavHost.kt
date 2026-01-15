package com.drsapps.trackerforlegominifiguresseries.presentation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.drsapps.trackerforlegominifiguresseries.presentation.hide_minifigures.HideMinifiguresScreen
import com.drsapps.trackerforlegominifiguresseries.presentation.hide_series.HideSeriesScreen
import com.drsapps.trackerforlegominifiguresseries.presentation.minifigure_details.MinifigureDetailsScreen
import com.drsapps.trackerforlegominifiguresseries.presentation.minifigures.MinifiguresScreen
import com.drsapps.trackerforlegominifiguresseries.presentation.minifigures_search.MinifiguresSearchScreen
import com.drsapps.trackerforlegominifiguresseries.presentation.series.SeriesScreen
import com.drsapps.trackerforlegominifiguresseries.presentation.series_details.SeriesDetailsScreen
import com.drsapps.trackerforlegominifiguresseries.presentation.series_image.SeriesImageScreen
import com.drsapps.trackerforlegominifiguresseries.presentation.series_search.SeriesSearchScreen
import com.drsapps.trackerforlegominifiguresseries.presentation.splash.SplashScreen
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Constants.MINIFIGURE_ID
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Constants.SERIES_ID
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Constants.TITLE
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Screen

@Composable
fun AppNavHost(
    navController: NavHostController,
    isNavigationRailShown: Boolean,
    isDataExistent: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isDataExistent) Screen.Series.route else Screen.SplashScreen.route,
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        }
    ) {
        composable(route = Screen.SplashScreen.route) {
            SplashScreen(
                onDataSynced = {
                    navController.navigate(Screen.Series.route) {
                        popUpTo(Screen.SplashScreen.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = Screen.Series.route) {
            SeriesScreen(
                onSeriesImageClick = { seriesId ->
                    navController.navigate(Screen.SeriesImage.route + "/$seriesId")
                },
                onSeriesClick = { seriesId, title ->
                    navController.navigate(Screen.SeriesDetails.route + "/$seriesId/$title")
                },
                onSearchClicked = {
                    navController.navigate(Screen.SeriesSearch.route)
                },
                onVisibilityClicked = {
                    navController.navigate(Screen.HideSeries.route)
                },
                isNavigationRailShown = isNavigationRailShown
            )
        }
        composable(
            route = Screen.SeriesImage.route + "/{$SERIES_ID}",
            arguments = listOf(
                navArgument(SERIES_ID) {
                    type = NavType.IntType
                }
            )
        ) {
            SeriesImageScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        composable(
            route = Screen.SeriesDetails.route + "/{$SERIES_ID}/{$TITLE}",
            arguments = listOf(
                navArgument(SERIES_ID) {
                    type = NavType.IntType
                },
                navArgument(TITLE) {
                    type = NavType.StringType
                }
            )
        ) { navBackStackEntry ->
            SeriesDetailsScreen(
                title = checkNotNull(navBackStackEntry.arguments?.getString(TITLE)),
                onMinifigureClick = { minifigureId, title ->
                    navController.navigate(
                        Screen.MinifigureDetails.route +
                            "/$minifigureId/$title")
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        composable(route = Screen.SeriesSearch.route) {
            SeriesSearchScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onSeriesImageClick = { seriesId ->
                    navController.navigate(Screen.SeriesImage.route + "/$seriesId")
                },
                onSeriesClick = { seriesId, title ->
                    navController.navigate(Screen.SeriesDetails.route + "/$seriesId/$title")
                }
            )
        }
        composable(route = Screen.HideSeries.route) {
            HideSeriesScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        composable(route = Screen.Minifigures.route) {
            MinifiguresScreen(
                onMinifigureClick = { minifigureId, title ->
                    navController.navigate(
                        Screen.MinifigureDetails.route +
                            "/$minifigureId/$title")
                },
                onSearchClicked = {
                    navController.navigate(Screen.SearchMinifigures.route)
                },
                onVisibilityClicked = {
                    navController.navigate(Screen.HideMinifigures.route)
                },
                isNavigationRailShown = isNavigationRailShown
            )
        }
        composable(
            route = Screen.MinifigureDetails.route +
                    "/{$MINIFIGURE_ID}/{$TITLE}",
            arguments = listOf(
                navArgument(MINIFIGURE_ID) {
                    type = NavType.IntType
                },
                navArgument(TITLE) {
                    type = NavType.StringType
                }
            )
        ) { navBackStackEntry ->
            MinifigureDetailsScreen(
                title = checkNotNull(navBackStackEntry.arguments?.getString(TITLE)),
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        composable(route = Screen.SearchMinifigures.route) {
            MinifiguresSearchScreen(
                onMinifigureClick = { minifigureId, title ->
                    navController.navigate(
                        Screen.MinifigureDetails.route +
                            "/$minifigureId/$title")
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        composable(route = Screen.HideMinifigures.route) {
            HideMinifiguresScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
//                                    composable(route = Screen.Settings.route) {
//                                        SettingsScreen()
//                                    }
    }
}