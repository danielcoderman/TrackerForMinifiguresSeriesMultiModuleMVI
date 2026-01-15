package com.drsapps.trackerforlegominifiguresseries.presentation.hide_series.components

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
import com.drsapps.trackerforlegominifiguresseries.domain.model.SeriesHiddenState
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme

@Composable
fun SeriesCheckbox(
    series: SeriesHiddenState,
    onHideSeries: (Int) -> Unit,
    onUnhideSeries: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .toggleable(
                value = series.numOfMinifigsHidden == series.numOfMinifigs,
                onValueChange = { checkedState ->
                    if (checkedState) {
                        onHideSeries(series.id)
                    } else {
                        onUnhideSeries(series.id)
                    }
                },
                role = Role.Checkbox
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = series.numOfMinifigsHidden == series.numOfMinifigs,
            onCheckedChange = null // null recommended for accessibility with screen readers
        )
        Text(
            text = series.name,
            modifier = Modifier.padding(start = 16.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SeriesCheckboxPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        SeriesCheckbox(
            series = SeriesHiddenState(id = 1, name = "Series 1", 16, 0),
            onHideSeries = {},
            onUnhideSeries = {}
        )
    }
}