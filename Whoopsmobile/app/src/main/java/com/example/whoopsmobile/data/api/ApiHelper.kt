package com.example.whoopsmobile.data.api

import com.example.whoopsmobile.BuildConfig
import android.util.Log
import com.example.whoopsmobile.model.Item
import com.example.whoopsmobile.model.Restaurant
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.io.OutputStreamWriter

object ApiConstants {
    val SUPABASE_URL: String get() = BuildConfig.SUPABASE_URL
    val SUPABASE_ANON_KEY: String get() = BuildConfig.SUPABASE_ANON_KEY
}

class ApiHelper {

    companion object {
        fun fallbackRestaurants(): List<Restaurant> = listOf(
            Restaurant(id = 1L, name = "Haga-vagninn", menuId = 1L, imageUrl = null)
        )
    }

    fun getItems(menuId: Long = 1L): ApiResult {

        return try {
            val query = "id=eq.$menuId&select=*,sections(*,items(*))"
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

    fun getRestaurants(): ApiResult {
        return try {
            val url = URL("${ApiConstants.SUPABASE_URL}/rest/v1/restaurants?select=*")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("apikey", ApiConstants.SUPABASE_ANON_KEY)
            connection.setRequestProperty("Authorization", "Bearer ${ApiConstants.SUPABASE_ANON_KEY}")
            connection.setRequestProperty("Accept", "application/json")
            val responseCode = connection.responseCode
            val response = if (responseCode in 200..299) connection.inputStream else connection.errorStream
                ?.let { BufferedReader(InputStreamReader(it)).use { r -> r.readText() } } ?: ""
            if (responseCode != 200) return ApiResult.Error("$responseCode")
            if (response.isBlank()) return ApiResult.Restaurants(emptyList())
            val arr = JSONArray(response)
            val list = (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                Restaurant(
                    id = o.getLong("id"),
                    name = o.getString("name"),
                    menuId = o.getLong("menu_id"),
                    imageUrl = o.optString("image_url").takeIf { it.isNotBlank() }
                )
            }
            ApiResult.Restaurants(list)
        } catch (e: Exception) {
            Log.e("API", "getRestaurants failed", e)
            ApiResult.Error("Error: ${e.message ?: "check logcat"}")
        }
    }

    /** Creates order in Supabase. Returns order id or null on failure. */
    fun createOrder(
        customerPhone: String,
        menuId: Long,
        totalIsk: Int,
        estimatedReadyAt: String?,
        items: List<OrderLine>
    ): Long? {
        return try {
            val orderBody = JSONObject().apply {
                put("customer_phone", customerPhone)
                put("menu_id", menuId)
                put("status", "RECEIVED")
                put("total_isk", totalIsk)
                if (estimatedReadyAt != null) put("estimated_ready_at", estimatedReadyAt)
            }
            val orderUrl = URL("${ApiConstants.SUPABASE_URL}/rest/v1/orders")
            val conn = orderUrl.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("apikey", ApiConstants.SUPABASE_ANON_KEY)
            conn.setRequestProperty("Authorization", "Bearer ${ApiConstants.SUPABASE_ANON_KEY}")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Prefer", "return=representation")
            OutputStreamWriter(conn.outputStream).use { it.write(orderBody.toString()) }
            val code = conn.responseCode
            val response = if (code in 200..299) conn.inputStream else conn.errorStream
                ?.let { BufferedReader(InputStreamReader(it)).use { r -> r.readText() } } ?: ""
            if (code !in 200..299) {
                Log.e("API", "createOrder failed $code $response")
                return null
            }
            val created = JSONArray(response)
            if (created.length() == 0) return null
            val orderId = created.getJSONObject(0).getLong("id")
            for (line in items) {
                val lineBody = JSONObject().apply {
                    put("order_id", orderId)
                    put("item_id", line.itemId)
                    put("item_name", line.itemName)
                    put("price_isk", line.priceIsk)
                    put("quantity", line.quantity)
                }
                val lineUrl = URL("${ApiConstants.SUPABASE_URL}/rest/v1/order_items")
                val lineConn = lineUrl.openConnection() as HttpURLConnection
                lineConn.requestMethod = "POST"
                lineConn.doOutput = true
                lineConn.setRequestProperty("apikey", ApiConstants.SUPABASE_ANON_KEY)
                lineConn.setRequestProperty("Authorization", "Bearer ${ApiConstants.SUPABASE_ANON_KEY}")
                lineConn.setRequestProperty("Content-Type", "application/json")
                OutputStreamWriter(lineConn.outputStream).use { it.write(lineBody.toString()) }
                if (lineConn.responseCode !in 200..299) {
                    Log.e("API", "createOrder order_items failed ${lineConn.responseCode}")
                }
            }
            orderId
        } catch (e: Exception) {
            Log.e("API", "createOrder failed", e)
            null
        }
    }
}

data class OrderLine(val itemId: Long, val itemName: String, val priceIsk: Int, val quantity: Int)
