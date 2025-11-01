
package com.example.weatherapp

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.theme.WeatherAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.ui.screens.CurrentWeatherScreen
import com.example.weatherapp.ui.screens.DailyForecastScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DisplayUI() {
    val navController = rememberNavController()
    var currentDestination by remember { mutableStateOf(Screen.CurrentWeather.route) }
    val mainViewModel: MainViewModel = viewModel()
    val currentLocation by mainViewModel.currentLocation.collectAsState() // CHANGED: removed WithLifecycle
    val isLoading by mainViewModel.isLoading.collectAsState() // CHANGED: removed WithLifecycle
    val error by mainViewModel.error.collectAsState() // CHANGED: removed WithLifecycle
    val context = LocalContext.current

    // Location permissions state
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // Track if we've already requested permissions
    var hasRequestedPermissions by remember { mutableStateOf(false) }

    // Initialize location service
    LaunchedEffect(Unit) {
        mainViewModel.initializeLocationService(context)
    }

    // Request permissions when the app starts
    LaunchedEffect(Unit) {
        if (!locationPermissionsState.allPermissionsGranted && !hasRequestedPermissions) {
            locationPermissionsState.launchMultiplePermissionRequest()
            hasRequestedPermissions = true
        }
    }

    // When permissions are granted, get device location
    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            mainViewModel.getDeviceLocation()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentLocation) },
                actions = {
                    // Refresh button
                    IconButton(
                        onClick = {
                            if (locationPermissionsState.allPermissionsGranted) {
                                mainViewModel.getDeviceLocation()
                            } else {
                                // Request permissions again if not granted
                                locationPermissionsState.launchMultiplePermissionRequest()
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh weather"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (locationPermissionsState.allPermissionsGranted || locationPermissionsState.shouldShowRationale) {
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Show permission rationale if needed
            if (!locationPermissionsState.allPermissionsGranted) {
                if (locationPermissionsState.shouldShowRationale) {
                    // User denied permission at least once - show rationale
                    PermissionRationale(
                        onRequestPermission = {
                            locationPermissionsState.launchMultiplePermissionRequest()
                        },
                        onUseDefaultLocation = {
                            mainViewModel.useDefaultLocation()
                        }
                    )
                } else if (!hasRequestedPermissions) {
                    // Haven't requested yet - show initial request
                    PermissionRequest(
                        onRequestPermission = {
                            locationPermissionsState.launchMultiplePermissionRequest()
                            hasRequestedPermissions = true
                        },
                        onUseDefaultLocation = {
                            mainViewModel.useDefaultLocation()
                            hasRequestedPermissions = true
                        }
                    )
                } else {
                    // Permissions denied without rationale - show fallback
                    PermissionDenied(
                        onUseDefaultLocation = {
                            mainViewModel.useDefaultLocation()
                        }
                    )
                }
            } else if (error != null && !isLoading) {
                // Show error message
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = error!!)
                    Button(
                        onClick = {
                            if (locationPermissionsState.allPermissionsGranted) {
                                mainViewModel.getDeviceLocation()
                            } else {
                                mainViewModel.useDefaultLocation()
                            }
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Try Again")
                    }
                }
            } else {
                // Permissions granted - show normal app content
                NavHostContainer(
                    navController = navController,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun PermissionRationale(
    onRequestPermission: () -> Unit,
    onUseDefaultLocation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Location Permission Required",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "This app needs location access to show weather for your current location. Your location data is only used to fetch weather information and is not stored.",
            modifier = Modifier.padding(vertical = 16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("Grant Permission")
        }
        Button(
            onClick = onUseDefaultLocation,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("Use Default Location (Halifax)")
        }
    }
}

@Composable
fun PermissionRequest(
    onRequestPermission: () -> Unit,
    onUseDefaultLocation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Weather App",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "To get weather for your current location, we need location permissions. You can also use a default location instead.",
            modifier = Modifier.padding(vertical = 16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("Use My Location")
        }
        Button(
            onClick = onUseDefaultLocation,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("Use Default Location")
        }
    }
}

@Composable
fun PermissionDenied(
    onUseDefaultLocation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Location Permission Denied",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "You've denied location permissions. You can still use the app with a default location, or grant permissions in app settings.",
            modifier = Modifier.padding(vertical = 16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Button(
            onClick = onUseDefaultLocation,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("Use Default Location (Halifax)")
        }
    }
}

@Composable
fun NavHostContainer(navController: NavHostController, modifier: Modifier = Modifier) {
    val mainViewModel: MainViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.CurrentWeather.route,
        modifier = modifier
    ) {
        composable(Screen.CurrentWeather.route) {
            CurrentWeatherScreen(viewModel = mainViewModel)
        }
        composable(Screen.Forecast.route) {
            DailyForecastScreen(viewModel = mainViewModel)
        }
    }
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