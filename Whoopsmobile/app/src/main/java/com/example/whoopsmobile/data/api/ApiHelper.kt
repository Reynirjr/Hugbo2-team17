package com.example.whoopsmobile.data.api

import android.util.Log
import com.example.whoopsmobile.model.Item
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object ApiConstants {
    const val SUPABASE_URL = "https://jomjklhroehwripfoift.supabase.co"
    const val SUPABASE_ANON_KEY = "REDACTED_SUPABASE_ANON_KEY"
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

            if (connection.responseCode != 200) {
                return ApiResult.Error("Server error: ${connection.responseCode}")
            }

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.readText()
            reader.close()
            Log.d("API_RESPONSE", response)
            val trimmed = response.trim()

            val items = mutableListOf<Item>()
            val menusArray = JSONArray(response)

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
                            imageData = null
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
            ApiResult.Error("Network error")
        }
    }
}
