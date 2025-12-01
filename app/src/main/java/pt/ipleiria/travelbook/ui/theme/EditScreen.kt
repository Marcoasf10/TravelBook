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
import pt.ipleiria.travelbook.components.DatePickerDialogs
import pt.ipleiria.travelbook.components.DateRangePickerRow
import pt.ipleiria.travelbook.components.DraggableNote

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(viewModel: LocationViewModel, navController: NavController, locationId: String) {
    var location by remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(locationId) {
        location = viewModel.locations.find { it.id == locationId }
    }

    if (location == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading or not found")
        }
        return
    }

    var name by remember { mutableStateOf(location!!.name) }
    var country by remember { mutableStateOf(location!!.country) }
    var notes by remember { mutableStateOf(location!!.notes.toMutableList()) }
    var status by remember { mutableStateOf(location!!.status) }
    var startDate by remember { mutableStateOf(location!!.startDate) }
    var endDate by remember { mutableStateOf(location!!.endDate) }

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    val dateFormatter = remember {
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
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
    ) { inner ->
        Column(
            modifier = Modifier.padding(inner).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- Name / Country Inputs ---
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("Country") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            DateRangePickerRow(
                startDate = startDate,
                endDate = endDate,
                onStartClick = { showStartPicker = true },
                onEndClick = { showEndPicker = true }
            )

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

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                notes.forEachIndexed { index, note ->
                    DraggableNote(
                        note = note,
                        notes = notes,
                        onValueChange = { newText -> notes[index] = newText },
                        onRemove = { notes.removeAt(index) }
                    )
                }
            }

            OutlinedButton(onClick = { notes.add("") }) {
                Text("+ Add AI suggestion")
            }

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

            // --- Delete / Save Buttons ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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