package com.wjoops.customer.viewmodels

import app.cash.turbine.test
import com.wjoops.customer.data.repositories.BasketRepository
import com.wjoops.customer.data.repositories.MenuRepository
import com.wjoops.customer.domain.models.Basket
import com.wjoops.customer.domain.models.MenuCategory
import com.wjoops.customer.domain.models.MenuItem
import com.wjoops.customer.domain.models.Restaurant
import com.wjoops.customer.domain.models.WaitTime
import com.wjoops.customer.domain.models.basketTotals
import com.wjoops.customer.location.LocationService
import com.wjoops.customer.testutil.MainDispatcherRule
import com.wjoops.customer.ui.viewmodel.MenuViewModel
import com.wjoops.customer.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MenuViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun `init loads restaurant`() = runTest {
        val vm = MenuViewModel(
            menuRepository = FakeMenuRepository(),
            basketRepository = FakeBasketRepository(),
            locationService = FakeLocationService(),
        )

        vm.state.test {
            // initial
            awaitItem()
            // after refresh completes (may take multiple emissions)
            var gotSuccess = false
            repeat(6) {
                val s = awaitItem()
                gotSuccess = gotSuccess || (s.restaurant is com.wjoops.customer.util.UiState.Success)
                if (gotSuccess) return@test
            }
            cancelAndConsumeRemainingEvents()
        }
    }
}

private class FakeMenuRepository : MenuRepository {
    override suspend fun getRestaurant() = ApiResult.Success(Restaurant("1", "Test", 0.0, 0.0))
    override suspend fun getMenu() = ApiResult.Success(emptyList<MenuCategory>() to emptyList<MenuItem>())
    override suspend fun getWaitTime() = ApiResult.Success(WaitTime(10))
}

private class FakeBasketRepository : BasketRepository {
    private val flow = MutableStateFlow(Basket(items = emptyList(), totals = basketTotals(emptyList())))
    override fun observeBasket() = flow.asStateFlow()
    override suspend fun addItem(item: MenuItem) {}
    override suspend fun removeItem(menuItemId: String) {}
    override suspend fun updateQty(menuItemId: String, quantity: Int) {}
    override suspend fun clear() {}
}

private class FakeLocationService : LocationService {
    override suspend fun getLastKnownLocation() = Result.success(null)
    override suspend fun requestSingleUpdate() = Result.success(null)
}
