import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pt.ipleiria.travelbook.Viewmodels.LocationViewModel
import pt.ipleiria.travelbook.ui.theme.CreateScreen
import pt.ipleiria.travelbook.ui.theme.EditScreen
import pt.ipleiria.travelbook.ui.theme.HomeScreen

@Composable
fun AppNavHost(navController: NavHostController, viewModel: LocationViewModel) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(viewModel = viewModel, navController = navController)
        }
        composable("create") {
            CreateScreen(viewModel = viewModel, navController = navController)
        }
        composable("edit/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            EditScreen(viewModel = viewModel, navController = navController, locationId = id)
        }
    }
}