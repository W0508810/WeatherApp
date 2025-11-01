
package com.example.weatherapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.R

import retrofit2.Response.error




@Composable
fun DailyForecastScreen(viewModel: MainViewModel) {
    // Collect the state flows as mutable state
    val weatherData by viewModel.weatherData.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Text("Loading forecast...")
                    }
                }
            }
            error != null -> {
                Text("Error: $error")
            }
            weatherData != null -> {
                val forecast = weatherData!!.forecast

                Text(
                    text = "7-Day Forecast",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn {
                    items(forecast.size) { index ->
                        val day = forecast[index]
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(text = "Date: ${day.date}")
                            Image(
                                painter = painterResource(id = R.drawable.ic_weather_placeholder),
                                contentDescription = "Weather Icon",
                                modifier = Modifier.size(80.dp)
                            )
                            Text(text = "High: ${day.highTemp}°C, Low: ${day.lowTemp}°C")
                            Text(text = "Condition: ${day.condition}")
                            Text(text = "Precipitation: ${day.precipitation}")
                            Text(text = "Wind: ${day.windDirection}")
                            Text(text = "Humidity: ${day.humidity}%")
                        }
                    }
                }
            }
            else -> {
                Text("No forecast data available")
            }
        }
    }
}


