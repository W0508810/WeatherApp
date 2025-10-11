package com.example.weatherapp.models

data class Weather(
    val current: Current,
    val forecast: List<Forecast>
)

data class Current(
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val precipitation: String = "Rain, 2mm", // From CurrentWeatherScreen
    val windDirection: String = "NW, 15 km/h" // From CurrentWeatherScreen
)

data class Forecast(
    val date: String, // Will be populated with actual dates
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val precipitation: String = "Rain, 0.4mm, 30%", // From DailyForecastScreen
    val windDirection: String = "NW, 20 km/h", // From DailyForecastScreen
    val highTemp: Double = temperature + 3, // From DailyForecastScreen
    val lowTemp: Double = temperature - 3 // From DailyForecastScreen
)