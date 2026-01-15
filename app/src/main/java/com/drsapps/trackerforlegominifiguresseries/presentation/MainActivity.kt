package com.drsapps.trackerforlegominifiguresseries.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.drsapps.trackerforlegominifiguresseries.presentation.util.navBarScreens
import com.drsapps.trackerforlegominifiguresseries.presentation.util.navRailScreens
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // The viewModels() delegate returns a lazy-initialized instance of the MainViewModel.
    // This means that the MainViewModel is not created immediately when the MainActivity class is
    // instantiated. Instead, it is created only when it is first accessed.
    private val mainViewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        // The NowInAndroid app (example for best practices) calls installSplashScreen() before
        // calling super.onCreate. However, from my testing switching the order doesn't seem to
        // cause issues.
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        // The NowInAndroid app calls enableEdgeToEdge after the call to installSplashScreen, and new
        // project boilerplate code places enableEdgeToEdge right after the call to super.onCreate.
        enableEdgeToEdge()

        // Keep the splash screen on-screen until hasExistingData has a non-null value. This
        // condition is evaluated each time the app needs to be redrawn so it should be fast to
        // avoid blocking the UI.
        splashScreen.setKeepOnScreenCondition {
            mainViewModel.hasExistingData.value == null
        }

        // Initialize the ReviewManager which is responsible for starting the in-app review flow.
        mainViewModel.initializeReviewManager(this)

        setContent {
            TrackerForLegoMinifiguresSeriesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val hasExistingData by mainViewModel.hasExistingData.collectAsStateWithLifecycle()
                    val shouldShowInAppReview by mainViewModel.shouldShowInAppReview.collectAsStateWithLifecycle()

                    LaunchedEffect(shouldShowInAppReview) {
                        if (shouldShowInAppReview) {
                            // TEST
                            Log.d("MainActivity", "Should show in-app review")
                            // TEST
                            mainViewModel.requestInAppReviewFlow(this@MainActivity)
                        }
                    }

                    val navController = rememberNavController()
                    val windowClass = calculateWindowSizeClass(activity = this)
                    val shouldShowNavigationRail = windowClass.widthSizeClass != WindowWidthSizeClass.Compact // Non-urgent: Consider using currentWindowAdaptiveInfo().windowSizeClass as seen here https://developer.android.com/develop/ui/compose/layouts/adaptive/use-window-size-classes#manage_layouts_with_window_size_classes

                    // Non-urgent: Can call isSystemInDarkTheme once here in the top level and then pass it down to composables that need it so that they can determine the color of icons, for example
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    Scaffold(
                        bottomBar = {
                            if (!shouldShowNavigationRail) {
                                currentDestination?.route?.let { currentDestinationRoute ->
                                    if (navBarScreens.any { it.route == currentDestinationRoute }) {
                                        BasicNavigationBar(
                                            currentDestinationRoute = currentDestinationRoute,
                                            onNavigationBarItemClick = { route ->
                                                navController.navigate(route) {
                                                    // Pop up to the start destination of the graph to
                                                    // avoid building up a large stack of destinations
                                                    // on the back stack as users select items
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    // Avoid multiple copies of the same destination when
                                                    // reselecting the same item
                                                    launchSingleTop = true
                                                    // Restore state when reselecting a previously selected item
                                                    restoreState = true
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        },
                        // This includes insets for the display cutout. Also, the WindowInsets.ime
                        // is excluded from the root Row composable so that the soft keyboard won't
                        // cause the Row composable and its children to move up and down when the
                        // keyboard shows up and disappears. Since the soft keyboard insets are
                        // excluded they can be handled by any descendants in order to react to them.
                        contentWindowInsets = WindowInsets.safeDrawing.exclude(WindowInsets.ime)
                    ) { paddingValues ->
                        Row(
                            modifier = Modifier
                                .padding(paddingValues)
                                .consumeWindowInsets(paddingValues) // This basically consumes the padding values so that descendants don't need to handle them since this ancestor already did.
                                .fillMaxSize()
                        ) {
                            if (shouldShowNavigationRail) {
                                currentDestination?.route?.let { currentDestinationRoute ->
                                    if (navRailScreens.any { it.route == currentDestinationRoute }) {
                                        BasicNavigationRail(
                                            currentDestinationRoute = currentDestinationRoute,
                                            onNavigationRailItemClick = { route ->
                                                navController.navigate(route) {
                                                    // Pop up to the start destination of the graph to
                                                    // avoid building up a large stack of destinations
                                                    // on the back stack as users select items
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    // Avoid multiple copies of the same destination when
                                                    // reselecting the same item
                                                    launchSingleTop = true
                                                    // Restore state when reselecting a previously selected item
                                                    restoreState = true
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                            if (hasExistingData != null) {
                                AppNavHost(
                                    navController = navController,
                                    isNavigationRailShown = shouldShowNavigationRail,
                                    isDataExistent = hasExistingData!!
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BasicNavigationBar(
    currentDestinationRoute: String,
    onNavigationBarItemClick: (route: String) -> Unit,
) {

    NavigationBar {
        navBarScreens.forEach { screen ->
            val isScreenSelected = screen.route == currentDestinationRoute
            NavigationBarItem(
                selected = isScreenSelected,
                onClick = {
                    onNavigationBarItemClick(screen.route)
                },
                icon = {
                    val screenIconResId = if (isScreenSelected) screen.selectedIconResourceId!! else screen.unselectedIconResourceId!!
                    Icon(
                        painter = painterResource(id = screenIconResId),
                        contentDescription = screen.title
                    )
                },
                label = {
                    Text(screen.title!!)
                }
            )
        }
    }

}

@Composable
fun BasicNavigationRail(
    currentDestinationRoute: String,
    onNavigationRailItemClick: (route: String) -> Unit,
) {

    NavigationRail {
        Spacer(modifier = Modifier.weight(0.5f))
        navRailScreens.forEach { screen ->
            val isScreenSelected = screen.route == currentDestinationRoute
            NavigationRailItem(
                selected = isScreenSelected,
                onClick = {
                    onNavigationRailItemClick(screen.route)
                },
                icon = {
                    val screenIconResId = if (isScreenSelected) screen.selectedIconResourceId!! else screen.unselectedIconResourceId!!
                    Icon(
                        painter = painterResource(id = screenIconResId),
                        contentDescription = screen.title
                    )
                },
                label = {
                    Text(screen.title!!)
                }
            )
        }
        Spacer(modifier = Modifier.weight(0.5f))
    }

}

@Preview
@Composable
fun BasicNavigationBarPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        BasicNavigationBar(
            currentDestinationRoute = navBarScreens[0].route,
            onNavigationBarItemClick = {}
        )
    }
}

@Preview
@Composable
fun BasicNavigationRailPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        BasicNavigationRail(
            currentDestinationRoute = navRailScreens[1].route,
            onNavigationRailItemClick = {}
        )
    }
}