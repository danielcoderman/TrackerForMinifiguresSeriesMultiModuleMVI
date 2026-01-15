package com.drsapps.trackerforlegominifiguresseries.presentation.minifigure_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.drsapps.trackerforlegominifiguresseries.R
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureInventoryItem
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme

@Composable
fun MinifigureInventoryItemOverlayCard(
    inventoryItem: MinifigureInventoryItem,
    onImageClick: (String, String) -> Unit,
    onCollectToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Used to prevent multiple actions if clicked rapidly
    val lifecycleOwner = LocalLifecycleOwner.current

    ElevatedCard(
        modifier = modifier,
        onClick = {
            val currentLifecycleState = lifecycleOwner.lifecycle.currentState
            if (currentLifecycleState.isAtLeast(Lifecycle.State.RESUMED)) {
                onImageClick(inventoryItem.imageUrl, inventoryItem.name)
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main image
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                model = inventoryItem.imageUrl,
                contentDescription = inventoryItem.name,
                error = painterResource(id = R.drawable.image_placeholder),
                contentScale = ContentScale.Fit
            )

            // Quantity badge in top-left corner
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
            ) {
                Text(
                    text = "${inventoryItem.quantity}x",
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .semantics {
                            contentDescription = "Quantity: ${inventoryItem.quantity}"
                        },
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )
            }

            // Collect button in top-right corner
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(36.dp),
                shape = CircleShape,
                color = if (inventoryItem.collected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                }
            ) {
                IconButton(
                    onClick = {
                        val currentLifecycleState = lifecycleOwner.lifecycle.currentState
                        if (currentLifecycleState.isAtLeast(Lifecycle.State.RESUMED)) {
                            onCollectToggle(inventoryItem.id)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = if (inventoryItem.collected) {
                            Icons.Default.Check
                        } else {
                            Icons.Default.Add
                        },
                        contentDescription = if (inventoryItem.collected) {
                            stringResource(R.string.remove_from_collected, inventoryItem.name)
                        } else {
                            stringResource(R.string.add_to_collected, inventoryItem.name)
                        },
                        tint = if (inventoryItem.collected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Semi-transparent overlay at bottom for part name
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = inventoryItem.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
fun MinifigureInventoryItemOverlayCardPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        MinifigureInventoryItemOverlayCard(
            inventoryItem = MinifigureInventoryItem(
                id = 1,
                name = "Droomantheons sare silvered plated sword",
                imageUrl = "",
                partUrl = "",
                quantity = 2,
                type = "Weapon",
                minifigureId = 1,
                collected = false
            ),
            onImageClick = { _, _ -> },
            onCollectToggle = { }
        )
    }
}