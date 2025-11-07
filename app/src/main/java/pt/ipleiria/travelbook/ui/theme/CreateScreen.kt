package pt.ipleiria.travelbook.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.ipleiria.travelbook.Models.Location
import pt.ipleiria.travelbook.Models.LocationStatus
import pt.ipleiria.travelbook.Viewmodels.LocationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(viewModel: LocationViewModel, navController: NavController) {
    var name by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    var isAiLoading by remember { mutableStateOf(false) }
    var showNameWarning by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    LaunchedEffect(viewModel.aiSuggestions) {
        if (viewModel.aiSuggestions.isNotEmpty()) {
            isAiLoading = false
            notes = viewModel.aiSuggestions.joinToString(separator = "\n")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Location") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (showNameWarning && it.isNotBlank()) showNameWarning = false
                },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = showNameWarning
            )

            if (showNameWarning) {
                Text(
                    text = "Please enter a location name first",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            OutlinedTextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("Country") },
                modifier = Modifier.fillMaxWidth()
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = if (isAiLoading) "" else notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (AI suggestions will appear here)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    enabled = !isAiLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                )

                if (isAiLoading) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generating AI suggestions...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Start Date: ${startDate?.let { dateFormatter.format(it) } ?: "Not set"}")
                TextButton(onClick = { showStartPicker = true }) {
                    Text("Pick")
                }
            }

            if (showStartPicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = startDate ?: System.currentTimeMillis()
                )
                DatePickerDialog(
                    onDismissRequest = { showStartPicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            startDate = datePickerState.selectedDateMillis
                            showStartPicker = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showStartPicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // --- End Date Picker ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("End Date: ${endDate?.let { dateFormatter.format(it) } ?: "Not set"}")
                TextButton(onClick = { showEndPicker = true }) {
                    Text("Pick")
                }
            }

            if (showEndPicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = endDate ?: System.currentTimeMillis()
                )
                DatePickerDialog(
                    onDismissRequest = { showEndPicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            endDate = datePickerState.selectedDateMillis
                            showEndPicker = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEndPicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        showNameWarning = true
                        return@Button
                    }

                    val location = Location(
                        name = name,
                        country = country,
                        notes = notes,
                        startDate = startDate,
                        endDate = endDate
                    )

                    viewModel.addLocation(location)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }

            OutlinedButton(
                onClick = {
                    if (name.isNotBlank()) {
                        isAiLoading = true
                        notes = ""
                        viewModel.getSuggestionsFor(name, country, startDate, endDate)
                    } else {
                        showNameWarning = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Suggest activities")
            }
        }
    }
}
