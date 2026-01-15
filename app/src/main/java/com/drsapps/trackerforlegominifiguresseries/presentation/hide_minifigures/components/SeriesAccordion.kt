package com.drsapps.trackerforlegominifiguresseries.presentation.hide_minifigures.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drsapps.trackerforlegominifiguresseries.domain.model.SeriesIdAndName
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme

@Composable
fun SeriesNameCard(
    series: SeriesIdAndName,
    onSeriesClick: (id: Int, name: String) -> Unit,
) {

    ElevatedCard(
        onClick = { onSeriesClick(series.id, series.name) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = series.name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }

}

@Preview
@Composable
fun SeriesAccordionPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        SeriesNameCard(
            series = SeriesIdAndName(1, "Series 1"),
            onSeriesClick = { _,_ -> }
        )
    }
}