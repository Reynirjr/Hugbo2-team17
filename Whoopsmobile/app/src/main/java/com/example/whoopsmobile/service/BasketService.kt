package com.example.whoopsmobile.service

import android.content.Context
import android.content.SharedPreferences
import com.example.whoopsmobile.model.Basket
import com.example.whoopsmobile.model.BasketItem
import com.example.whoopsmobile.model.Item
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Holds the current basket and provides add, remove, update quantity, total.
 * Persists basket to local storage (SharedPreferences) so it survives app restarts.
 * Aligns with UML: BasketService manages one Basket; used by MenuFragment, ItemDetailsFragment, BasketFragment.
 */
object BasketService {

    private const val PREFS_NAME = "whoops_basket"
    private const val KEY_BASKET_JSON = "basket_json"

    private val basket = Basket()
    private var prefs: SharedPreferences? = null

    /** Current menu items for lookup when opening item details by id. Set by MenuFragment after load. */
    var currentMenuItems: List<Item> = emptyList()
        private set

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            loadBasket()
        }
    }

    private fun requirePrefs(): SharedPreferences = prefs ?: error("BasketService not initialized. Call init(context) in MainActivity.")

    private fun loadBasket() {
        val json = requirePrefs().getString(KEY_BASKET_JSON, null) ?: return
        try {
            val arr = JSONArray(json)
            basket.items.clear()
            for (i in 0 until arr.length()) {
                val ob = arr.getJSONObject(i)
                val item = parseItem(ob.getJSONObject("item"))
                basket.items.add(
                    BasketItem(
                        id = ob.optString("id", UUID.randomUUID().toString()),
                        item = item,
                        quantity = ob.optInt("quantity", 1).coerceAtLeast(1)
                    )
                )
            }
        } catch (_: Exception) {
            // Ignore corrupt or old-format data
        }
    }

    private fun saveBasket() {
        val arr = JSONArray()
        for (bi in basket.items) {
            arr.put(
                JSONObject().apply {
                    put("id", bi.id)
                    put("quantity", bi.quantity)
                    put("item", itemToJson(bi.item))
                }
            )
        }
        requirePrefs().edit().putString(KEY_BASKET_JSON, arr.toString()).apply()
    }

    private fun itemToJson(item: Item): JSONObject = JSONObject().apply {
        put("id", item.id)
        put("name", item.name)
        put("description", item.description)
        put("priceIsk", item.priceIsk)
        put("available", item.available)
        put("tags", item.tags)
        put("imageData", item.imageData ?: JSONObject.NULL)
        item.estimatedWaitTimeMinutes?.let { put("estimatedWaitTimeMinutes", it) }
    }

    private fun parseItem(ob: JSONObject): Item = Item(
        id = ob.getInt("id"),
        name = ob.optString("name", ""),
        description = ob.optString("description", ""),
        priceIsk = ob.optInt("priceIsk", 0),
        available = ob.optBoolean("available", true),
        tags = ob.optString("tags", ""),
        imageData = ob.optString("imageData", null).takeIf { !it.isNullOrEmpty() },
        estimatedWaitTimeMinutes = ob.optInt("estimatedWaitTimeMinutes", -1).takeIf { it >= 0 }
    )

    fun setCurrentMenuItems(items: List<Item>) {
        currentMenuItems = items
    }

    fun getItemById(id: Int): Item? = currentMenuItems.find { it.id == id }

    fun getBasket(): Basket = basket

    fun addItem(item: Item, quantity: Int) {
        require(quantity > 0) { "Quantity must be positive" }
        val existing = basket.items.find { it.item.id == item.id }
        if (existing != null) {
            val idx = basket.items.indexOf(existing)
            basket.items[idx] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            basket.items.add(
                BasketItem(id = UUID.randomUUID().toString(), item = item, quantity = quantity)
            )
        }
        saveBasket()
    }

    fun removeItem(basketItem: BasketItem) {
        basket.items.remove(basketItem)
        saveBasket()
    }

    fun removeItem(item: Item) {
        basket.items.removeAll { it.item.id == item.id }
        saveBasket()
    }

    fun updateQuantity(basketItem: BasketItem, quantity: Int) {
        if (quantity <= 0) {
            basket.items.remove(basketItem)
            saveBasket()
            return
        }
        val idx = basket.items.indexOf(basketItem)
        if (idx >= 0) basket.items[idx] = basketItem.copy(quantity = quantity)
        saveBasket()
    }

    fun calculateTotal(): Int = basket.items.sumOf { it.lineTotal }

    fun clearBasket() {
        basket.items.clear()
        saveBasket()
    }

    fun totalItemCount(): Int = basket.totalItemCount
}
