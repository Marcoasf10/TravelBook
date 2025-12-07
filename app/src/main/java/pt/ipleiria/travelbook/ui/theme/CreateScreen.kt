package pt.ipleiria.travelbook.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.ipleiria.travelbook.Models.Location
import pt.ipleiria.travelbook.Viewmodels.LocationViewModel
import pt.ipleiria.travelbook.components.DatePickerDialogs
import pt.ipleiria.travelbook.components.DateRangePickerRow
import pt.ipleiria.travelbook.components.DraggableNote
import pt.ipleiria.travelbook.components.LoadingOverlay
import pt.ipleiria.travelbook.components.LocationMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(viewModel: LocationViewModel, navController: NavController) {
    var name by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    val notes = remember { mutableStateListOf<String>() }

    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    var isAiLoading by remember { mutableStateOf(false) }
    var showNameWarning by remember { mutableStateOf(false) }

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
                title = { Text("Add Location") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
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
                            viewModel.getSuggestion(name, country, startDate, endDate, notes)
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
            }
        }
    }
}