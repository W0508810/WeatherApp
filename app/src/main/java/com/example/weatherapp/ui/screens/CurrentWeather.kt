
package com.example.weatherapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.R

@Composable
fun CurrentWeatherScreen(viewModel: MainViewModel) {
    // Collect the state flows as mutable state
    val weatherData by viewModel.weatherData.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator()
                Text("Loading weather data...")
            }
            error != null -> {
                Text("Error: $error")
            }
            weatherData != null -> {
                val currentWeather = weatherData!!.current

                Image(
                    painter = painterResource(id = R.drawable.ic_weather_placeholder),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(100.dp)
                )

                Text(
                    text = "${currentWeather.temperature}°C",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(
                    text = currentWeather.condition,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Weather details
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    WeatherDetailItem("Precipitation", currentWeather.precipitation)
                    WeatherDetailItem("Wind", currentWeather.windDirection)
                    WeatherDetailItem("Humidity", "${currentWeather.humidity}%")
                }
            }
            else -> {
                Text("No weather data available")
            }
        }
    }
}

@Composable
fun WeatherDetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(text = value)
    }
}


