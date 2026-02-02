package com.wjoops.customer.data.repositories

import com.wjoops.customer.data.datastore.BasketSnapshotStore
import com.wjoops.customer.domain.models.Basket
import com.wjoops.customer.domain.models.BasketItem
import com.wjoops.customer.domain.models.MenuItem
import com.wjoops.customer.domain.models.basketTotals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class BasketRepositoryImpl @Inject constructor(
    private val basketSnapshotStore: BasketSnapshotStore,
) : BasketRepository {
    private val basketFlow = MutableStateFlow(emptyBasket())

    override fun observeBasket(): Flow<Basket> = basketFlow.asStateFlow()

    override suspend fun addItem(item: MenuItem) {
        val current = basketFlow.value
        val existing = current.items.firstOrNull { it.menuItem.id == item.id }
        val nextItems = if (existing == null) {
            current.items + BasketItem(menuItem = item, quantity = 1)
        } else {
            current.items.map {
                if (it.menuItem.id == item.id) it.copy(quantity = it.quantity + 1) else it
            }
        }
        update(nextItems)
    }

    override suspend fun removeItem(menuItemId: String) {
        val nextItems = basketFlow.value.items.filterNot { it.menuItem.id == menuItemId }
        update(nextItems)
    }

    override suspend fun updateQty(menuItemId: String, quantity: Int) {
        if (quantity <= 0) {
            removeItem(menuItemId)
            return
        }
        val nextItems = basketFlow.value.items.mapNotNull {
            if (it.menuItem.id != menuItemId) it else it.copy(quantity = quantity)
        }
        update(nextItems)
    }

    override suspend fun clear() {
        update(emptyList())
    }

    private suspend fun update(items: List<BasketItem>) {
        val totals = basketTotals(items)
        val next = Basket(items = items, totals = totals)
        basketFlow.value = next
        basketSnapshotStore.storeBasketSnapshot(next)
    }

    private fun emptyBasket(): Basket = Basket(items = emptyList(), totals = basketTotals(emptyList()))
}

