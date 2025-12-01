package pt.ipleiria.travelbook.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DateRangePickerRow(
    startDate: Long?,
    endDate: Long?,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DateField(
            label = "Start Date",
            date = startDate,
            onClick = onStartClick,
            modifier = Modifier.weight(1f)
        )
        DateField(
            label = "End Date",
            date = endDate,
            onClick = onEndClick,
            modifier = Modifier.weight(1f)
        )
    }
}