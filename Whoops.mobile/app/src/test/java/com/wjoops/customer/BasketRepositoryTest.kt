package com.wjoops.customer

import app.cash.turbine.test
import com.wjoops.customer.data.datastore.AppDataStore
import com.wjoops.customer.data.network.defaultJson
import com.wjoops.customer.data.repositories.BasketRepositoryImpl
import com.wjoops.customer.data.repositories.FakeData
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.assertEquals

class BasketRepositoryTest {
    @Test
    fun `addItem increases basket count`() = runTest {
        // Minimal test: uses in-memory basket; DataStore is not exercised here.
        val repo = BasketRepositoryImpl(
            dataStore = FakeAppDataStore(),
        )

        repo.observeBasket().test {
            val first = awaitItem()
            assertEquals(0, first.items.size)
            repo.addItem(FakeData.items.first())
            val second = awaitItem()
            assertEquals(1, second.items.sumOf { it.quantity })
            cancelAndConsumeRemainingEvents()
        }
    }
}

private class FakeAppDataStore : AppDataStore(
    appContext = androidx.test.core.app.ApplicationProvider.getApplicationContext(),
    json = defaultJson(),
)
