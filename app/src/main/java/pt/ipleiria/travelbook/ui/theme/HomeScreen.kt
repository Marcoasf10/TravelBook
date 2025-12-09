package pt.ipleiria.travelbook.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.ipleiria.travelbook.Models.LocationStatus
import pt.ipleiria.travelbook.R
import pt.ipleiria.travelbook.Viewmodels.LocationViewModel
import pt.ipleiria.travelbook.components.LocationCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: LocationViewModel, navController: NavController) {
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    val planned = remember(viewModel.locations) {
        viewModel.locations.filter { it.status == LocationStatus.PLANNED }.sortedBy { it.startDate }
    }
    val visited = remember(viewModel.locations) {
        viewModel.locations.filter { it.status == LocationStatus.VISITED }.sortedBy { it.startDate }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Planned", "Visited")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "TravelBook",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_outline_book),
                            contentDescription = "Book Icon",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create") },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_location_alt),
                    contentDescription = "Add",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            val currentList = if (selectedTabIndex == 0) planned else visited

            Box(modifier = Modifier.fillMaxSize()) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (currentList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No voyages yet in this category.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(currentList.size) { idx ->
                            val loc = currentList[idx]
                            LocationCard(
                                location = loc,
                                dateFormatter = dateFormatter,
                                onClick = { navController.navigate("edit/${loc.id}") }
                            )
                        }
                    }
                }
            }
        }
    }
}