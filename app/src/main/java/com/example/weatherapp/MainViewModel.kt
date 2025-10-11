package com.example.weatherapp

import androidx.lifecycle.ViewModel
import com.example.weatherapp.models.Current
import com.example.weatherapp.models.Forecast
import com.example.weatherapp.models.Weather

class MainViewModel : ViewModel() {
    // Create a Weather object with placeholder data
    val weatherData = Weather(
        current = Current(
            temperature = 19.0,
            condition = "Partly Cloudy",
            humidity = 50,
            windSpeed = 5.0
        ),
        forecast = listOf(
            Forecast(
                date = "Today",
                temperature = 19.0,
                condition = "Sunny",
                humidity = 30,
                windSpeed = 1.0
            ),
            Forecast(
                date = "Tomorrow",
                temperature = 24.0,
                condition = "Rainy",
                humidity = 80,
                windSpeed = 15.0
            ),
            Forecast(
                date = "Day after",
                temperature = 18.0,
                condition = "Cloudy",
                humidity = 65,
                windSpeed = 8.0
            )
        )
    )
}
