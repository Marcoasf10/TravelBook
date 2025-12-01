package pt.ipleiria.travelbook.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogs(
    showStartPicker: Boolean,
    showEndPicker: Boolean,
    startDate: Long?,
    endDate: Long?,
    onStartDismiss: () -> Unit,
    onEndDismiss: () -> Unit,
    onStartSelected: (Long?) -> Unit,
    onEndSelected: (Long?) -> Unit,
) {
    if (showStartPicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = startDate ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = onStartDismiss,
            confirmButton = {
                TextButton(onClick = {
                    onStartSelected(state.selectedDateMillis)
                    onStartDismiss()
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = onStartDismiss) { Text("Cancel") }
            }
        ) { DatePicker(state = state) }
    }

    if (showEndPicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = endDate ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = onEndDismiss,
            confirmButton = {
                TextButton(onClick = {
                    onEndSelected(state.selectedDateMillis)
                    onEndDismiss()
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = onEndDismiss) { Text("Cancel") }
            }
        ) { DatePicker(state = state) }
    }
}