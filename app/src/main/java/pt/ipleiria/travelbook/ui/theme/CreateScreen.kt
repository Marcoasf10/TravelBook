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
import pt.ipleiria.travelbook.components.DatePickerDialogs
import pt.ipleiria.travelbook.components.DateRangePickerRow
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
    var invalidDates by remember { mutableStateOf(false) }

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
                    IconButton(onClick = { notes.removeAt(index) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove")
                    }
                }
            }

            item {
                LoadingOverlay(isLoading = isAiLoading)
                OutlinedButton(
                    onClick = {
                        if (name.isNotBlank()) {
                            isAiLoading = true
                            viewModel.getSuggestion(name, country, startDate, endDate, notes)
                        } else showNameWarning = true
                    },
                    enabled = !isAiLoading
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

                        if (startDate != null && endDate != null && startDate!! > endDate!!) {
                            invalidDates = true
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