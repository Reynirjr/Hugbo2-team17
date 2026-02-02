package com.wjoops.customer.repositories

import app.cash.turbine.test
import com.wjoops.customer.data.datastore.BasketSnapshotStore
import com.wjoops.customer.data.repositories.BasketRepositoryImpl
import com.wjoops.customer.data.repositories.FakeData
import com.wjoops.customer.testutil.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BasketRepositoryImplTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun `addItem updates basket flow`() = runTest {
        val repo = BasketRepositoryImpl(basketSnapshotStore = NoopBasketSnapshotStore())

        repo.observeBasket().test {
            val empty = awaitItem()
            assertEquals(0, empty.items.size)

            repo.addItem(FakeData.items.first())
            val updated = awaitItem()
            assertEquals(1, updated.items.sumOf { it.quantity })

            cancelAndConsumeRemainingEvents()
        }
    }
}

private class NoopBasketSnapshotStore : BasketSnapshotStore {
    override suspend fun storeBasketSnapshot(basket: com.wjoops.customer.domain.models.Basket) = Unit
}
