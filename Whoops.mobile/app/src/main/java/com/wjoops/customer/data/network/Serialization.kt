package com.wjoops.customer.data.network

import kotlinx.serialization.json.Json

fun defaultJson(): Json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
}
