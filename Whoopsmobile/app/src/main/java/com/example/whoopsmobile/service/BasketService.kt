package com.example.whoopsmobile.service

import com.example.whoopsmobile.model.Basket
import com.example.whoopsmobile.model.BasketItem
import com.example.whoopsmobile.model.Item
import java.util.UUID

/**
 * Holds the current basket and provides add, remove, update quantity, total.
 * Aligns with UML: BasketService manages one Basket; used by MenuFragment, ItemDetailsFragment, BasketFragment.
 */
object BasketService {

    private val basket = Basket()

    /** Current menu items for lookup when opening item details by id. Set by MenuFragment after load. */
    var currentMenuItems: List<Item> = emptyList()
        private set

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
    }

    fun removeItem(basketItem: BasketItem) {
        basket.items.remove(basketItem)
    }

    fun removeItem(item: Item) {
        basket.items.removeAll { it.item.id == item.id }
    }

    fun updateQuantity(basketItem: BasketItem, quantity: Int) {
        if (quantity <= 0) {
            basket.items.remove(basketItem)
            return
        }
        val idx = basket.items.indexOf(basketItem)
        if (idx >= 0) basket.items[idx] = basketItem.copy(quantity = quantity)
    }

    fun calculateTotal(): Int = basket.items.sumOf { it.lineTotal }

    fun clearBasket() {
        basket.items.clear()
    }

    fun totalItemCount(): Int = basket.totalItemCount
}
