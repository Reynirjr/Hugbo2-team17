package com.example.whoopsmobile.data.api

import com.example.whoopsmobile.BuildConfig
import android.util.Log
import com.example.whoopsmobile.model.Item
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object ApiConstants {
    val SUPABASE_URL: String get() = BuildConfig.SUPABASE_URL
    val SUPABASE_ANON_KEY: String get() = BuildConfig.SUPABASE_ANON_KEY
}

class ApiHelper {

    fun getItems(): ApiResult {

        return try {
            val query = "select=*,sections(*,items(*))"
            val url = URL("${ApiConstants.SUPABASE_URL}/rest/v1/menus?$query")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("apikey", ApiConstants.SUPABASE_ANON_KEY)
            connection.setRequestProperty("Authorization", "Bearer ${ApiConstants.SUPABASE_ANON_KEY}")
            connection.setRequestProperty("Accept", "application/json")

            val responseCode = connection.responseCode
            val stream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
            val response = stream?.let { BufferedReader(InputStreamReader(it)).use { r -> r.readText() } } ?: ""
            Log.d("API", "Response code: $responseCode body: ${response.take(500)}")

            if (responseCode != 200) {
                val msg = try {
                    if (response.trimStart().startsWith("{")) {
                        JSONObject(response).optString("message", response)
                    } else response
                } catch (_: Exception) { response }
                return ApiResult.Error("Server $responseCode: ${msg.ifEmpty { "check logcat" } }")
            }

            val items = mutableListOf<Item>()
            if (response.isBlank()) {
                return ApiResult.Empty
            }
            val menusArray = try {
                JSONArray(response)
            } catch (e: Exception) {
                Log.e("API", "Parse failed for: ${response.take(200)}", e)
                return ApiResult.Error("Invalid response")
            }

            if (menusArray.length() == 0) {
                return ApiResult.Empty
            }

            val menuObject = menusArray.getJSONObject(0)
            val sectionsArray = menuObject.optJSONArray("sections") ?: return ApiResult.Empty

            for (i in 0 until sectionsArray.length()) {
                val section = sectionsArray.getJSONObject(i)
                val itemsArray = section.optJSONArray("items") ?: continue

                for (j in 0 until itemsArray.length()) {
                    val itemObj = itemsArray.getJSONObject(j)
                    items.add(
                        Item(
                            id = itemObj.getInt("id"),
                            name = itemObj.getString("name"),
                            description = itemObj.optString("description", ""),
                            priceIsk = itemObj.optInt("price_isk", itemObj.optInt("priceIsk", 0)),
                            available = itemObj.optBoolean("available", true),
                            tags = itemObj.optString("tags", ""),
                            imageData = null,
                            estimatedWaitTimeMinutes = itemObj.optInt("estimated_wait_time_minutes", 0).takeIf { it > 0 }
                        )
                    )
                }
            }

            if (items.isEmpty()) {
                return ApiResult.Empty
            } else {
                return ApiResult.Success(items)
            }

        } catch (e: Exception) {
            Log.e("API", "getItems failed", e)
            ApiResult.Error("Error: ${e.message ?: "check logcat"}")
        }
    }
}
