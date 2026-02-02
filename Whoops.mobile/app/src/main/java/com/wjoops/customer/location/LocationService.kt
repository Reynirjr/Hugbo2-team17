package com.wjoops.customer.location

import android.location.Location

interface LocationService {
    suspend fun getLastKnownLocation(): Result<Location?>
    suspend fun requestSingleUpdate(): Result<Location?>
}
