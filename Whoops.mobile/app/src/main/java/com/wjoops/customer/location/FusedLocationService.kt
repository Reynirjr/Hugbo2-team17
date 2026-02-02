package com.wjoops.customer.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class FusedLocationService @Inject constructor(
    private val appContext: Context,
) : LocationService {

    @SuppressLint("MissingPermission")
    override suspend fun getLastKnownLocation(): Result<Location?> {
        return try {
            val client = LocationServices.getFusedLocationProviderClient(appContext)
            val location = suspendCancellableCoroutine<Location?> { cont ->
                client.lastLocation
                    .addOnSuccessListener { cont.resume(it) }
                    .addOnFailureListener { cont.resume(null) }
            }
            Result.success(location)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun requestSingleUpdate(): Result<Location?> {
        // TODO: implement a single high-accuracy update if needed.
        return getLastKnownLocation()
    }
}
