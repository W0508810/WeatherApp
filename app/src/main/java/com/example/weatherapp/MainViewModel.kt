
package com.example.weatherapp

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.Current
import com.example.weatherapp.models.Forecast
import com.example.weatherapp.models.Weather
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.services.LocationService
import com.example.weatherapp.services.WeatherService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel : ViewModel() {

    private val _weatherData = MutableStateFlow<Weather?>(null)
    val weatherData = _weatherData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Add a flow for current location to be used in the UI
    private val _currentLocation = MutableStateFlow("Loading location...")
    val currentLocation = _currentLocation.asStateFlow()

    // Add flow for location coordinates
    private val _deviceLocation = MutableStateFlow<Location?>(null)
    val deviceLocation = _deviceLocation.asStateFlow()

    // Retrofit instance
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.weatherapi.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Weather service
    private val weatherService: WeatherService = retrofit.create(WeatherService::class.java)

    // API Key
    private val apiKey = "ec5a5de780ef466b903154359252910"

    private var locationService: LocationService? = null

    fun initializeLocationService(context: Context) {
        locationService = LocationService(context)
    }

    init {
        // Start with default location text
        _currentLocation.value = "Weather App"
    }

    // ... existing methods ...

    fun useDefaultLocation() {
        loadWeatherData("Halifax")
    }

    // Load weather by city name
    fun loadWeatherData(location: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = weatherService.getWeatherForecast(
                    apiKey = apiKey,
                    location = location,
                    days = 7
                )
                _weatherData.value = mapApiResponseToWeather(response)
                // Update the current location with data from API
                _currentLocation.value = "${response.location.name}, ${response.location.region ?: response.location.country ?: ""}"
            } catch (e: Exception) {
                _error.value = "Failed to load weather data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Load weather by coordinates (latitude and longitude)
    fun loadWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Use lat,long format for the API
                val locationString = "$latitude,$longitude"
                val response = weatherService.getWeatherForecast(
                    apiKey = apiKey,
                    location = locationString,
                    days = 7
                )
                _weatherData.value = mapApiResponseToWeather(response)
                // Update the current location with data from API
                _currentLocation.value = "${response.location.name}, ${response.location.region ?: response.location.country ?: ""}"
            } catch (e: Exception) {
                _error.value = "Failed to load weather data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // THIS IS WHERE getCurrentLocation() IS ACTUALLY USED
    fun getDeviceLocation() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // This is the call to getCurrentLocation() from LocationService
                val location = locationService?.getCurrentLocation()
                if (location != null) {
                    _deviceLocation.value = location
                    // Automatically load weather for device location
                    loadWeatherData(location.latitude, location.longitude)
                    _currentLocation.value = "Getting your location..."
                } else {
                    _error.value = "Unable to get device location. Using default location."
                    // Fallback to default location if device location fails
                    loadWeatherData("Halifax")
                    _isLoading.value = false
                }
            } catch (e: SecurityException) {
                _error.value = "Location permission required. Using default location."
                // Fallback to default location if permissions not granted
                loadWeatherData("Halifax")
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to get location: ${e.message}. Using default location."
                // Fallback to default location on other errors
                loadWeatherData("Halifax")
                _isLoading.value = false
            }
        }
    }

    // Add a method to manually refresh location and weather
    fun refreshWeather() {
        getDeviceLocation()
    }

    private fun mapApiResponseToWeather(response: WeatherResponse): Weather {
        // Map location
        val location = com.example.weatherapp.models.Location(
            name = response.location.name,
            region = response.location.region,
            country = response.location.country
        )

        // Map current weather
        val current = Current(
            temperature = response.current.tempC,
            condition = response.current.condition.text,
            humidity = response.current.humidity,
            windSpeed = response.current.windKph,
            precipitation = "Rain, ${response.current.precipMm}mm",
            windDirection = "${response.current.windDir}, ${response.current.windKph} km/h"
        )

        // Map forecast
        val forecast = response.forecast.forecastday.map { forecastDay ->
            Forecast(
                date = forecastDay.date,
                temperature = forecastDay.day.avgTempC,
                condition = forecastDay.day.condition.text,
                humidity = forecastDay.day.avgHumidity.toInt(),
                windSpeed = forecastDay.day.maxWindKph,
                precipitation = "Rain, ${forecastDay.day.totalPrecipMm}mm, ${forecastDay.day.dailyChanceOfRain}%",
                windDirection = "NW, ${forecastDay.day.maxWindKph} km/h",
                highTemp = forecastDay.day.maxTempC,
                lowTemp = forecastDay.day.minTempC
            )
        }


        return Weather(location = location, current = current, forecast = forecast)
    }
}