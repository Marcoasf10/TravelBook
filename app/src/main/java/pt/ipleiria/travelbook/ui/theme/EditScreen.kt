package pt.ipleiria.travelbook.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import pt.ipleiria.travelbook.components.DatePickerDialogs
import pt.ipleiria.travelbook.components.DateRangePickerRow
import pt.ipleiria.travelbook.components.DraggableNote
import pt.ipleiria.travelbook.components.LocationMap
import pt.ipleiria.travelbook.components.LoadingOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(viewModel: LocationViewModel, navController: NavController, locationId: String) {
    var location by remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(locationId) {
        location = viewModel.locations.find { it.id == locationId }
    }

    if (location == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading or not found")
        }
        return
    }

    var name by remember { mutableStateOf(location!!.name) }
    var country by remember { mutableStateOf(location!!.country) }
    val notes = remember { mutableStateListOf<String>() }
    var status by remember { mutableStateOf(location!!.status) }
    var startDate by remember { mutableStateOf(location!!.startDate) }
    var endDate by remember { mutableStateOf(location!!.endDate) }

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    var showNameWarning by remember { mutableStateOf(false) }
    var isAiLoading by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.aiSuggestions) {
        if (viewModel.aiSuggestions.isNotEmpty()) {
            isAiLoading = false
            notes.add(viewModel.aiSuggestions.first())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Location") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {

            item {
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
            }

            item {
                if (showNameWarning) {
                    Text(
                        text = "Please enter a location name first",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Country") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                DateRangePickerRow(
                    startDate = startDate,
                    endDate = endDate,
                    onStartClick = { showStartPicker = true },
                    onEndClick = { showEndPicker = true }
                )
            }

            item {
                DatePickerDialogs(
                    showStartPicker = showStartPicker,
                    showEndPicker = showEndPicker,
                    startDate = startDate,
                    endDate = endDate,
                    onStartDismiss = { showStartPicker = false },
                    onEndDismiss = { showEndPicker = false },
                    onStartSelected = { startDate = it },
                    onEndSelected = { endDate = it }
                )
            }

            items(notes.size) { index ->
                DraggableNote(
                    note = notes[index],
                    notes = notes,
                    onValueChange = { newText -> notes[index] = newText },
                    onRemove = { notes.removeAt(index) }
                )
            }

            item {
                LoadingOverlay(isLoading = isAiLoading)
                OutlinedButton(
                    onClick = {
                        if (name.isNotBlank()) {
                            isAiLoading = true
                            viewModel.getOneSuggestion(name, country, startDate, endDate)
                        } else showNameWarning = true
                    }
                ) {
                    Text("+ Add AI suggestion")
                }
            }

            item {
                LocationMap(
                    locationName = name,
                    country = country
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Status: ${if (status == LocationStatus.PLANNED) "Planned" else "Visited"}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = (status == LocationStatus.VISITED),
                        onCheckedChange = {
                            status = if (it) LocationStatus.VISITED else LocationStatus.PLANNED
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.deleteLocation(locationId)
                            navController.popBackStack()
                        }
                    ) {
                        Text("Delete")
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val updated = location!!.copy(
                                name = name,
                                country = country,
                                notes = notes,
                                status = status,
                                startDate = startDate,
                                endDate = endDate
                            )
                            viewModel.updateLocation(updated)
                            navController.popBackStack()
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}