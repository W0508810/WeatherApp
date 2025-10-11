package com.example.weatherapp.ui.screens  // Fix typo: com.exmaple -> com.example

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.R

@Composable
fun DailyForecastScreen(viewModel: MainViewModel) {
    val forecast = viewModel.weatherData.forecast

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(forecast.size) { index ->
            val day = forecast[index]
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(text = "Date: ${day.date}")
                Image(
                    painter = painterResource(id = R.drawable.ic_weather_placeholder),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(80.dp)
                )
                Text(text = "High: ${day.temperature + 3}°C, Low: ${day.temperature - 3}°C")
                Text(text = "Condition: ${day.condition}")
                Text(text = "Precipitation: Rain, 0.4mm, 30%")
                Text(text = "Wind: NW, 20 km/h")
                Text(text = "Humidity: ${day.humidity}%")
            }
        }
    }
}
