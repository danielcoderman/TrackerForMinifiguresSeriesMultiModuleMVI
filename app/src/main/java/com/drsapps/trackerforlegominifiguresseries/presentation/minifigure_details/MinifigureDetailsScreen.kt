package com.drsapps.trackerforlegominifiguresseries.presentation.minifigure_details

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import coil.compose.AsyncImage
import com.drsapps.trackerforlegominifiguresseries.R
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureInventoryItem
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureWithSeriesName
import com.drsapps.trackerforlegominifiguresseries.presentation.minifigure_details.components.ImageViewerModal
import com.drsapps.trackerforlegominifiguresseries.presentation.minifigure_details.components.MinifigureDetailsTopAppBar
import com.drsapps.trackerforlegominifiguresseries.presentation.minifigure_details.components.MinifigureInventoryScrollableSection
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme

@Composable
fun MinifigureDetailsScreen(
    viewModel: MinifigureDetailsViewModel = hiltViewModel(),
    title: String,
    onNavigateBack: () -> Unit
) {

    val minifigure by viewModel.minifigure.collectAsStateWithLifecycle()
    val minifigureParts by viewModel.minifigureParts.collectAsStateWithLifecycle()
    val minifigureAccessories by viewModel.minifigureAccessories.collectAsStateWithLifecycle()

    MinifigureDetailsScreen(
        title = title,
        minifigure = minifigure,
        minifigureParts = minifigureParts,
        minifigureAccessories = minifigureAccessories,
        onToggleCollectedState = viewModel::toggleMinifigureCollectedStateAndResetWishlistedState,
        onToggleWishlistedState = viewModel::toggleMinifigureWishlistedStateAndResetCollectedState,
        onToggleFavoriteState = viewModel::toggleMinifigureFavoriteState,
        onToggleInventoryItemCollectedState = viewModel::toggleMinifigInvItemCollectedStateAndKeepMinifigCollectedStateInSync,
        onNavigateBack = onNavigateBack
    )

}

@Composable
fun MinifigureDetailsScreen(
    title: String,
    minifigure: MinifigureWithSeriesName?,
    minifigureParts: List<MinifigureInventoryItem>,
    minifigureAccessories: List<MinifigureInventoryItem>,
    onToggleCollectedState: () -> Unit,
    onToggleWishlistedState: () -> Unit,
    onToggleFavoriteState: () -> Unit,
    onToggleInventoryItemCollectedState: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
) {
    // State for image viewer modal TODO("Move to the ViewModel?")
    var showImageViewer by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf("") }
    var selectedItemName by remember { mutableStateOf("") }
    var selectedPartUrl by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            MinifigureDetailsTopAppBar(
                title = title,
                isCollected = minifigure?.collected,
                isWishlisted = minifigure?.wishListed,
                isFavorite = minifigure?.favorite,
                onToggleCollectedState = onToggleCollectedState,
                onToggleWishlistedState = onToggleWishlistedState,
                onToggleFavoriteState = onToggleFavoriteState,
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->

        // Used to get the height of the screen in dp units
        val configuration = LocalConfiguration.current
        val orientation = configuration.orientation

        // TODO: the layout is pretty good although it could be improved with a sort of surface background
        // behind the minifigure inventory items sections and maybe even behind the minifigure image section and the text.
        // Maybe with this layout change the 50/50 division (0.5f) between the minifigure image section and the inventory items section will look great.
        if (orientation == Configuration.ORIENTATION_LANDSCAPE && windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            Row(
                modifier = Modifier.padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
            ) {
                if (minifigure != null) {
                    // Minifigure image section
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Main minifigure image
                        AsyncImage(
                            modifier = Modifier.weight(weight = 1f)
                                .aspectRatio(1f),
                            model = minifigure.imageUrl,
                            contentDescription = null, // description not needed because of text below
                            error = painterResource(id = R.drawable.image_placeholder),
                            contentScale = ContentScale.Fit
                        )

                        // Minifigure name and series
                        Text(
                            text = minifigure.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = minifigure.seriesName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    // Column of minifigure parts section
                    Column(
                        modifier = Modifier.weight(1f) // Non-urgent: Add a light rounded background to better separate the main minifigure image and the inventory section.
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Parts section with horizontal scrolling
                        if (minifigureParts.isNotEmpty()) {
                            MinifigureInventoryScrollableSection(
                                sectionTitle = "Minifigure Parts",
                                inventoryItems = minifigureParts,
                                onImageClick = { imageUrl, itemName ->
                                    selectedImageUrl = imageUrl
                                    selectedItemName = itemName
                                    selectedPartUrl = minifigureParts.find { it.imageUrl == imageUrl }?.partUrl
                                    showImageViewer = true
                                },
                                onCollectToggle = onToggleInventoryItemCollectedState
                            )
                        }

                        // Accessories section with horizontal scrolling
                        if (minifigureAccessories.isNotEmpty()) {
                            MinifigureInventoryScrollableSection(
                                sectionTitle = "Other/Alternative Parts",
                                inventoryItems = minifigureAccessories,
                                onImageClick = { imageUrl, itemName ->
                                    selectedImageUrl = imageUrl
                                    selectedItemName = itemName
                                    selectedPartUrl = minifigureAccessories.find { it.imageUrl == imageUrl }?.partUrl
                                    showImageViewer = true
                                },
                                onCollectToggle = onToggleInventoryItemCollectedState,
                                modifier = Modifier.padding(top = 24.dp)
                            )
                        }

                        // Show message if no inventory items
                        if (minifigureParts.isEmpty() && minifigureAccessories.isEmpty()) {
                            Text(
                                text = "No inventory items found for this minifigure. Pull down to refresh on the series screen.", // TODO: Use a string resource
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .semantics(mergeDescendants = true) {} // This tells the accessibility service to read the content descriptions of this composables "children" all in one go (one focusable entity instead of two) // TODO: I think I should remove the accessibilty functionality around my app since the target audience will require seeing the minifigure or series images (Also keeps the code simpler).
            ) {
                if (minifigure != null) {
                    Column(
                        modifier = Modifier.padding(paddingValues)
                            .consumeWindowInsets(paddingValues)
                            .fillMaxWidth(), // Note: The height will be "infinity" (height isn't fixed) since the parent composable is scrollable
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Minifigure image section
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Main minifigure image
                            AsyncImage(
                                modifier = Modifier.fillMaxWidth()
                                    .aspectRatio(1f),
                                model = minifigure.imageUrl,
                                contentDescription = null, // description not needed because of text below
                                error = painterResource(id = R.drawable.image_placeholder),
                                contentScale = ContentScale.Fit
                            )

                            // Minifigure name and series
                            Text(
                                text = minifigure.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = minifigure.seriesName,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Parts section with horizontal scrolling
                        if (minifigureParts.isNotEmpty()) {
                            MinifigureInventoryScrollableSection(
                                sectionTitle = "Minifigure Parts",
                                inventoryItems = minifigureParts,
                                onImageClick = { imageUrl, itemName ->
                                    selectedImageUrl = imageUrl
                                    selectedItemName = itemName
                                    selectedPartUrl = minifigureParts.find { it.imageUrl == imageUrl }?.partUrl
                                    showImageViewer = true
                                },
                                onCollectToggle = onToggleInventoryItemCollectedState
                            )
                        }

                        // Accessories section with horizontal scrolling
                        if (minifigureAccessories.isNotEmpty()) {
                            MinifigureInventoryScrollableSection(
                                sectionTitle = "Other/Alternative Parts",
                                inventoryItems = minifigureAccessories,
                                onImageClick = { imageUrl, itemName ->
                                    selectedImageUrl = imageUrl
                                    selectedItemName = itemName
                                    selectedPartUrl = minifigureAccessories.find { it.imageUrl == imageUrl }?.partUrl
                                    showImageViewer = true
                                },
                                onCollectToggle = onToggleInventoryItemCollectedState,
                                modifier = Modifier.padding(top = 24.dp)
                            )
                        }

                        // Show message if no inventory items
                        if (minifigureParts.isEmpty() && minifigureAccessories.isEmpty()) {
                            Text(
                                text = "No inventory items found for this minifigure. Pull down to refresh on the series screen.", // TODO: Since usually every minifigure should have an inventory, unless not provided by Rebrickable, I should consider adding a refresh button below this text. I could do this once I write some logic that allows new series and minifigures to be inserted without the corresponding minifigure inventory items.
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        // Image viewer modal
        if (showImageViewer) {
            ImageViewerModal(
                imageUrl = selectedImageUrl,
                itemName = selectedItemName,
                partUrl = selectedPartUrl,
                onDismiss = { showImageViewer = false }
            )
        }
    }
}

@Preview
@Composable
fun MinifigureDetailsScreenPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        MinifigureDetailsScreen(
            title = "Minifig 1",
            minifigure = MinifigureWithSeriesName(
                id = 1,
                name = "Minifig 1",
                imageUrl = "",
                collected = false,
                wishListed = false,
                favorite = true,
                seriesName = "Series 1"
            ),
            minifigureParts = listOf(
                MinifigureInventoryItem(
                    id = 1,
                    name = "Minifig 1 Head",
                    imageUrl = "",
                    partUrl = "",
                    quantity = 1,
                    type = "Part",
                    minifigureId = 1,
                    collected = true
                ),
                MinifigureInventoryItem(
                    id = 2,
                    name = "Minifig 1 Torso",
                    imageUrl = "",
                    partUrl = "",
                    quantity = 1,
                    type = "Part",
                    minifigureId = 1,
                    collected = false
                )
            ),
            minifigureAccessories = listOf(
                MinifigureInventoryItem(
                    id = 3,
                    name = "Droomantheons sare silvered plated sword",
                    imageUrl = "",
                    partUrl = "",
                    quantity = 2,
                    type = "Accessory",
                    minifigureId = 1,
                    collected = false
                ),
                MinifigureInventoryItem(
                    id = 4,
                    name = "Knight Shield",
                    imageUrl = "",
                    partUrl = "",
                    quantity = 1,
                    type = "Accessory",
                    minifigureId = 1,
                    collected = true
                ),
                MinifigureInventoryItem(
                    id = 5,
                    name = "Royal Cape",
                    imageUrl = "",
                    partUrl = "",
                    quantity = 1,
                    type = "Accessory",
                    minifigureId = 1,
                    collected = false
                )
            ),
            onToggleCollectedState = {},
            onToggleWishlistedState = {},
            onToggleFavoriteState = {},
            onToggleInventoryItemCollectedState = { },
            onNavigateBack = {}
        )
    }
}

@Preview(name = "Tablet", device = "spec:width=1280dp,height=800dp,dpi=240") // Non-urgent: Check if there's a better way to make a tablet-like preview
@Composable
fun MinifigureDetailsScreenNoInventoryPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        MinifigureDetailsScreen(
            title = "Minifig 1",
            minifigure = MinifigureWithSeriesName(
                id = 1,
                name = "Minifig 1",
                imageUrl = "",
                collected = false,
                wishListed = false,
                favorite = true,
                seriesName = "Series 1"
            ),
            minifigureParts = listOf(),
            minifigureAccessories = listOf(),
            onToggleCollectedState = {},
            onToggleWishlistedState = {},
            onToggleFavoriteState = {},
            onToggleInventoryItemCollectedState = { },
            onNavigateBack = {}
        )
    }
}