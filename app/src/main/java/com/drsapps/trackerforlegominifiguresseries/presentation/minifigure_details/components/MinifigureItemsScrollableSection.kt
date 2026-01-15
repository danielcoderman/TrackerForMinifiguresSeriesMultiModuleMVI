package com.drsapps.trackerforlegominifiguresseries.presentation.minifigure_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureInventoryItem
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme

@Composable
fun MinifigureInventoryScrollableSection(
    sectionTitle: String,
    inventoryItems: List<MinifigureInventoryItem>,
    onImageClick: (String, String) -> Unit,
    onCollectToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (inventoryItems.isEmpty()) {
        return
    }

    val listState = rememberLazyListState()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Section title
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )

        // Scrollable row of cards
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp), // TODO: Maybe add some padding on the LazyRow itself (with a background or surface) instead of just on the content
        ) {
            items(inventoryItems) { item ->
                MinifigureInventoryItemOverlayCard(
                    inventoryItem = item,
                    onImageClick = onImageClick,
                    onCollectToggle = onCollectToggle,
                    modifier = Modifier.size(140.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun MinifigureInventoryScrollableSectionPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        val sampleParts = listOf(
            MinifigureInventoryItem(
                id = 1,
                name = "Minifig Head Gith Warlock",
                imageUrl = "",
                partUrl = "",
                quantity = 1,
                type = "Part",
                minifigureId = 1,
                collected = true
            ),
            MinifigureInventoryItem(
                id = 2,
                name = "Minifig Torso with Armor",
                imageUrl = "",
                partUrl = "",
                quantity = 1,
                type = "Part",
                minifigureId = 1,
                collected = false
            ),
            MinifigureInventoryItem(
                id = 3,
                name = "Minifig Legs Dark Blue",
                imageUrl = "",
                partUrl = "",
                quantity = 1,
                type = "Part",
                minifigureId = 1,
                collected = false
            ),
            MinifigureInventoryItem(
                id = 4,
                name = "Minifig Hair Brown",
                imageUrl = "",
                partUrl = "",
                quantity = 1,
                type = "Part",
                minifigureId = 1,
                collected = true
            )
        )

        Column {
            MinifigureInventoryScrollableSection(
                sectionTitle = "Parts",
                inventoryItems = sampleParts,
                onImageClick = { _, _ -> },
                onCollectToggle = { }
            )
        }
    }
}