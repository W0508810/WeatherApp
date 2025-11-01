
package com.example.weatherapp.models

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val location: Location,
    val current: CurrentWeather,
    val forecast: ForecastData
)

data class CurrentWeather(
    @SerializedName("temp_c") val tempC: Double,
    val condition: Condition,
    @SerializedName("wind_kph") val windKph: Double,
    @SerializedName("wind_dir") val windDir: String,
    @SerializedName("precip_mm") val precipMm: Double,
    val humidity: Int,
    val cloud: Int,
    @SerializedName("feelslike_c") val feelsLikeC: Double
)

data class ForecastData(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: DayForecast,
    val astro: Astro,
    val hour: List<HourForecast>
)

data class DayForecast(
    @SerializedName("maxtemp_c") val maxTempC: Double,
    @SerializedName("mintemp_c") val minTempC: Double,
    @SerializedName("avgtemp_c") val avgTempC: Double,
    @SerializedName("maxwind_kph") val maxWindKph: Double,
    @SerializedName("totalprecip_mm") val totalPrecipMm: Double,
    @SerializedName("avgvis_km") val avgVisKm: Double,
    @SerializedName("avghumidity") val avgHumidity: Double,
    val condition: Condition,
    @SerializedName("daily_chance_of_rain") val dailyChanceOfRain: Int,
    @SerializedName("daily_chance_of_snow") val dailyChanceOfSnow: Int
)

data class HourForecast(
    val time: String,
    @SerializedName("temp_c") val tempC: Double,
    val condition: Condition,
    @SerializedName("wind_kph") val windKph: Double,
    @SerializedName("wind_dir") val windDir: String,
    @SerializedName("precip_mm") val precipMm: Double,
    val humidity: Int,
    val cloud: Int,
    @SerializedName("feelslike_c") val feelsLikeC: Double,
    @SerializedName("chance_of_rain") val chanceOfRain: Int,
    @SerializedName("chance_of_snow") val chanceOfSnow: Int
)

data class Condition(
    val text: String,
    val icon: String,
    val code: Int
)

data class Astro(
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    val moonset: String,
    @SerializedName("moon_phase") val moonPhase: String,
    @SerializedName("moon_illumination") val moonIllumination: String
)
