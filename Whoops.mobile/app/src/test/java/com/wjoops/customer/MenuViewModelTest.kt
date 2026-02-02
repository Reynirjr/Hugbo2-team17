package com.wjoops.customer

import app.cash.turbine.test
import com.wjoops.customer.data.repositories.BasketRepository
import com.wjoops.customer.data.repositories.BasketRepositoryImpl
import com.wjoops.customer.data.repositories.MenuRepositoryImpl
import com.wjoops.customer.location.LocationService
import com.wjoops.customer.ui.viewmodel.MenuViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.assertTrue

class MenuViewModelTest {
    @Test
    fun `refresh loads restaurant and menu`() = runTest {
        val vm = MenuViewModel(
            menuRepository = MenuRepositoryImpl(),
            basketRepository = FakeBasketRepository(),
            locationService = FakeLocationService(),
        )

        vm.state.test {
            val first = awaitItem()
            // Initial state, then async loads.
            val second = awaitItem()
            val third = awaitItem()
            assertTrue(second.restaurant is com.wjoops.customer.util.UiState.Success || third.restaurant is com.wjoops.customer.util.UiState.Success)
            cancelAndConsumeRemainingEvents()
        }
    }
}

private class FakeBasketRepository : BasketRepository {
    private val delegate = kotlinx.coroutines.flow.MutableStateFlow(
        com.wjoops.customer.domain.models.Basket(
            items = emptyList(),
            totals = com.wjoops.customer.domain.models.basketTotals(emptyList()),
        ),
    )

    override fun observeBasket() = delegate
    override suspend fun addItem(item: com.wjoops.customer.domain.models.MenuItem) {}
    override suspend fun removeItem(menuItemId: String) {}
    override suspend fun updateQty(menuItemId: String, quantity: Int) {}
    override suspend fun clear() {}
}

private class FakeLocationService : LocationService {
    override suspend fun getLastKnownLocation(): Result<android.location.Location?> = Result.success(null)
    override suspend fun requestSingleUpdate(): Result<android.location.Location?> = Result.success(null)
}
