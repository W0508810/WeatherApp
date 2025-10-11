package com.example.weatherapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.R

@Composable
fun CurrentWeatherScreen(viewModel: MainViewModel) {
    val currentWeather = viewModel.weatherData.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_weather_placeholder),
            contentDescription = "Weather Icon",
            modifier = Modifier.size(100.dp)
        )
        Text(text = "Condition: ${currentWeather.condition}")
        Text(text = "Temperature: ${currentWeather.temperature}°C")
        Text(text = "Precipitation: Rain, 2mm")
        Text(text = "Wind: NW, 15 km/h")
        Text(text = "Humidity: ${currentWeather.humidity}%")
    }
}


