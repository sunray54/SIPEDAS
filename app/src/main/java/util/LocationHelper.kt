package util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationHelper(private val activity: Activity) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

    fun getCurrentLocation(callback: (Location?) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                callback(location)
            }.addOnFailureListener {
                callback(null)
            }
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
            )
            callback(null)
        }
    }

    companion object {
        const val REQUEST_LOCATION = 1001
    }
}