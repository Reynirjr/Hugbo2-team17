package com.example.whoopsmobile.data.api

import com.example.whoopsmobile.model.Item
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object ApiConstants {
    const val BASE_URL = "https://hugbo2-team17.onrender.com"
}

class ApiHelper {

    fun getItems(): List<Item> {

        val url = URL("${ApiConstants.BASE_URL}/api/menus")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val response = reader.readText()
        reader.close()

        val items = mutableListOf<Item>()
        val menusArray = JSONArray(response)

        if (menusArray.length() > 0) {

            val menuObject = menusArray.getJSONObject(0)
            val sectionsArray = menuObject.getJSONArray("sections")

            for (i in 0 until sectionsArray.length()) {

                val section = sectionsArray.getJSONObject(i)
                val itemsArray = section.getJSONArray("items")

                for (j in 0 until itemsArray.length()) {

                    val itemObj = itemsArray.getJSONObject(j)

                    val item = Item(
                        id = itemObj.getInt("id"),
                        name = itemObj.getString("name"),
                        description = itemObj.getString("description"),
                        priceIsk = itemObj.getInt("priceIsk"),
                        available = itemObj.getBoolean("available"),
                        tags = itemObj.getString("tags"),
                        imageData = null
                    )

                    items.add(item)
                }
            }
        }

        return items
    }
}
