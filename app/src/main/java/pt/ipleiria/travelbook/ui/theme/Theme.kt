package pt.ipleiria.travelbook.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FlightBlue = Color(0xFF0B6FF2)
private val LightBlue = Color(0xFFc8e5ff)
private val Green = Color(0xFF009E60)
private val LightGreen = Color(0xFFB4DCC3)

private val LightColors = lightColorScheme(
    primary = FlightBlue,
    onPrimary = Color.White,
    surface = LightBlue,
    onSurface = Color.Black,
    secondary = Green,
    surfaceVariant = LightGreen
)

@Composable
fun TravelBookTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}