package com.example.weatherapp.models

data class Location(
    val name: String,
    val region: String? = null,
    val country: String? = null
)