
package com.example.weatherapp.services

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.tasks.await

class LocationService(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getCurrentLocation(): Location? {
        return try {
            // Check if we have location permissions
            if (!hasLocationPermissions()) {
                throw SecurityException("Location permissions not granted")
            }

            // Get the last known location first (it's faster)
            val lastLocation = fusedLocationClient.lastLocation.await()

            // If last location is not available or too old, get a fresh one
            if (lastLocation != null && isLocationFresh(lastLocation)) {
                lastLocation
            } else {
                // Get fresh location with high accuracy
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    object : CancellationToken() {
                        override fun onCanceledRequested(listener: OnTokenCanceledListener) =
                            CancellationTokenSource().token

                        override fun isCancellationRequested() = false
                    }
                ).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationFresh(location: Location): Boolean {
        // Consider location fresh if it's less than 5 minutes old
        val locationAge = System.currentTimeMillis() - location.time
        return locationAge < 5 * 60 * 1000 // 5 minutes in milliseconds
    }
}

