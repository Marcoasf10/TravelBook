package pt.ipleiria.travelbook.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.ipleiria.travelbook.Models.Location
import pt.ipleiria.travelbook.Viewmodels.LocationViewModel
import pt.ipleiria.travelbook.components.ConfirmationDialog
import pt.ipleiria.travelbook.components.DatePickerDialogs
import pt.ipleiria.travelbook.components.DateRangePickerRow
import pt.ipleiria.travelbook.components.LocationMap
import pt.ipleiria.travelbook.components.LoadingOverlay
import pt.ipleiria.travelbook.components.ToggleStatus

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
    val notes = remember { mutableStateListOf<String>().apply { addAll(location!!.notes) } }
    var status by remember { mutableStateOf(location!!.status) }
    var startDate by remember { mutableStateOf(location!!.startDate) }
    var endDate by remember { mutableStateOf(location!!.endDate) }

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    var showNameWarning by remember { mutableStateOf(false) }
    var isAiLoading by remember { mutableStateOf(false) }
    var invalidDates by remember { mutableStateOf(false) }
    var showDeleteLocationDialog by remember { mutableStateOf(false) }
    var noteToDeleteIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(viewModel.aiSuggestion) {
        if (!viewModel.aiSuggestion.isNullOrEmpty()) {
            notes.add(viewModel.aiSuggestion!!)
        }
        isAiLoading = false
        viewModel.clearAiSuggestion()
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
                    onEndClick = { showEndPicker = true },
                    invalidDates = invalidDates
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    OutlinedTextField(
                        value = notes[index],
                        onValueChange = { newText -> notes[index] = newText },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { noteToDeleteIndex = index }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove")
                    }
                }
            }

            item {
                LoadingOverlay(isLoading = isAiLoading)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { notes.add("") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+ Add note")
                    }

                    OutlinedButton(
                        onClick = {
                            if (name.isNotBlank()) {
                                isAiLoading = true
                                viewModel.getSuggestion(name, country, startDate, endDate, notes)
                            } else showNameWarning = true
                        },
                        enabled = !isAiLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+ Add AI suggestion")
                    }
                }
            }

            item {
                LocationMap(
                    locationName = name,
                    country = country
                )
            }

            item {
                ToggleStatus(
                    status = status,
                    onStatusChange = { newStatus -> status = newStatus }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { showDeleteLocationDialog = true }
                    ) {
                        Text("Delete")
                    }

                    ConfirmationDialog(
                        showDialog = showDeleteLocationDialog,
                        title = "Delete Location",
                        message = "Are you sure you want to delete this location?",
                        onConfirm = {
                            viewModel.deleteLocation(locationId)
                            navController.popBackStack()
                        },
                        onDismiss = { showDeleteLocationDialog = false }
                    )

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (name.isBlank()) {
                                showNameWarning = true
                                return@Button
                            }

                            if (startDate != null && endDate != null && startDate!! > endDate!!) {
                                invalidDates = true
                                return@Button
                            }

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

        noteToDeleteIndex?.let { index ->
            ConfirmationDialog(
                showDialog = true,
                title = "Delete Note",
                message = "Are you sure you want to delete this note?",
                onConfirm = { notes.removeAt(index) },
                onDismiss = { noteToDeleteIndex = null }
            )
        }
    }
}