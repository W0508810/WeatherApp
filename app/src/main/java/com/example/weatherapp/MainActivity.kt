package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                DisplayUI()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayUI() {
    val navController = rememberNavController()
    var currentDestination by remember { mutableStateOf(Screen.CurrentWeather.route) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Halifax, Nova Scotia") }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.LocationOn, contentDescription = "Current Weather") },
                    label = { Text("Current") },
                    selected = currentDestination == Screen.CurrentWeather.route,
                    onClick = {
                        navController.navigate(Screen.CurrentWeather.route) {
                            popUpTo(Screen.CurrentWeather.route) { inclusive = true }
                        }
                        currentDestination = Screen.CurrentWeather.route
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Forecast") },
                    label = { Text("Forecast") },
                    selected = currentDestination == Screen.Forecast.route,
                    onClick = {
                        navController.navigate(Screen.Forecast.route) {
                            popUpTo(Screen.CurrentWeather.route) { inclusive = false }
                        }
                        currentDestination = Screen.Forecast.route
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHostContainer(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@Composable
fun NavHostContainer(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.CurrentWeather.route,
        modifier = modifier
    ) {
        composable(Screen.CurrentWeather.route) {
            CurrentWeatherScreen()
        }
        composable(Screen.Forecast.route) {
            ForecastScreen()
        }
    }
}

@Composable
fun CurrentWeatherScreen() {
    Text(
        text = "Current Weather Screen",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun ForecastScreen() {
    Text(
        text = "Weather Forecast Screen",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(16.dp)
    )
}

// Sealed class for defining screens
sealed class Screen(val route: String) {
    object CurrentWeather : Screen("current_weather")
    object Forecast : Screen("forecast")
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeatherAppTheme {
        Greeting("Android")
    }
}

@Composable
fun ScrollableColumn(){
    val scrollState = rememberScrollState()
}

@Preview(showBackground = true)
@Composable
fun DisplayUIPreview() {
    WeatherAppTheme {
        DisplayUI()
    }
}