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
    const val BASE_URL = "https://hugbo2-team17.onrender.com"
}

class ApiHelper {

    fun getItems(): ApiResult {

<<<<<<< HEAD
        return try {
=======
        val url = URL("${ApiConstants.BASE_URL}/api/menus")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
>>>>>>> ccbc3966efe0136cb845b66adb8530e0c188604b

            val url = URL("https://hugbo2-team17.onrender.com/api/menus")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            if (connection.responseCode != 200) {
                return ApiResult.Error("Server error: ${connection.responseCode}")
            }

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.readText()
            reader.close()
            Log.d("API_RESPONSE", response)
            val trimmed = response.trim()

            // API returned error JSON
            if (trimmed.startsWith("{")) {
                val obj = JSONObject(trimmed)
                if (obj.has("error")) {
                    return ApiResult.Error(obj.getString("error"))
                }
            }

            val items = mutableListOf<Item>()
            val menusArray = JSONArray(response)

            if (menusArray.length() == 0) {
                return ApiResult.Empty
            }

            val menuObject = menusArray.getJSONObject(0)
            val sectionsArray = menuObject.getJSONArray("sections")

            for (i in 0 until sectionsArray.length()) {
                val section = sectionsArray.getJSONObject(i)
                val itemsArray = section.getJSONArray("items")

                for (j in 0 until itemsArray.length()) {

                    val itemObj = itemsArray.getJSONObject(j)

                    items.add(
                        Item(
                            id = itemObj.getInt("id"),
                            name = itemObj.getString("name"),
                            description = itemObj.getString("description"),
                            priceIsk = itemObj.getInt("priceIsk"),
                            available = itemObj.getBoolean("available"),
                            tags = itemObj.getString("tags"),
                            imageData = null
                        )
                    )
                }
            }

            if (items.isEmpty()) {
                ApiResult.Empty
            } else {
                ApiResult.Success(items)
            }

        } catch (e: Exception) {
            ApiResult.Error("Network error")
        }
    }
}
