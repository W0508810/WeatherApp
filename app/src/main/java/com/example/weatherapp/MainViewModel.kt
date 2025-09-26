package com.example.weatherapp

import androidx.lifecycle.ViewModel
import com.example.weatherapp.models.Weather

annotation class Weather(
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double
)
class MainViewModel : ViewModel() {
    val currentWeather = Weather(
        temperature = 19.0,
        condition = "Partly Cloudy",
        humidity = 50,
        windSpeed = 5.0
    )

    val forecast = listOf(
        Weather(19.0, "Sunny", 30, 1.0),
        Weather(24.0, "Rainy", 80, 15.0),
        Weather(18.0, "Cloudy", 65, 8.0)
    )
}
