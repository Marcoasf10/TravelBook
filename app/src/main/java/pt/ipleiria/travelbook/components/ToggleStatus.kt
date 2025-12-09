package pt.ipleiria.travelbook.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.ipleiria.travelbook.Models.LocationStatus

@Composable
fun ToggleStatus(
    status: LocationStatus,
    onStatusChange: (LocationStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val bgColor = if (status == LocationStatus.VISITED)
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else
            MaterialTheme.colorScheme.surfaceVariant

        val textColor = if (status == LocationStatus.VISITED)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.onSurfaceVariant

        Box(
            modifier = Modifier
                .background(bgColor, shape = MaterialTheme.shapes.small)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (status == LocationStatus.PLANNED) "Planned" else "Visited",
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Switch(
            checked = (status == LocationStatus.VISITED),
            onCheckedChange = {
                onStatusChange(if (it) LocationStatus.VISITED else LocationStatus.PLANNED)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}