
package com.example.weatherapp.models

data class Weather(
    val location: Location, // Added location
    val current: Current,
    val forecast: List<Forecast>
)

data class Current(
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val precipitation: String = "Rain, 2mm",
    val windDirection: String = "NW, 15 km/h"
)

data class Forecast(
    val date: String,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val precipitation: String = "Rain, 0.4mm, 30%",
    val windDirection: String = "NW, 20 km/h",
    val highTemp: Double = temperature + 3,
    val lowTemp: Double = temperature - 3
)
