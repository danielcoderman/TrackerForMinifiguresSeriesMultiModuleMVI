package com.drsapps.trackerforlegominifiguresseries.presentation.hide_minifigures.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureHiddenState
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme

@Composable
fun MinifigureCheckbox(
    minifigure: MinifigureHiddenState,
    onClick: (Int) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .toggleable(
                value = minifigure.hidden,
                onValueChange = {
                    onClick(minifigure.id)
                },
                role = Role.Checkbox
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = minifigure.hidden,
            onCheckedChange = null // null recommended for accessibility with screen readers
        )
        Text(
            text = minifigure.name,
            modifier = Modifier.padding(start = 16.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
    }

}

@Preview(showBackground = true)
@Composable
fun MinifigureCheckboxPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        MinifigureCheckbox(
            minifigure = MinifigureHiddenState(
                1,
                "Minifig 1",
                true
            ),
            onClick = {}
        )
    }
}