package pt.ipleiria.travelbook.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DateRangePickerRow(
    startDate: Long?,
    endDate: Long?,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit,
    invalidDates: Boolean = false
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
    if (invalidDates) {
        Text(
            text = "Start date should be prior to or equal to End date",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
        )
    }
}