package pt.ipleiria.travelbook

import AppNavHost
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import pt.ipleiria.travelbook.ui.theme.TravelBookTheme
import pt.ipleiria.travelbook.Viewmodels.LocationViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: LocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TravelBookTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController, viewModel = viewModel)
            }
        }
    }
}