package com.wjoops.customer.viewmodels

import com.wjoops.customer.data.repositories.BasketRepository
import com.wjoops.customer.data.repositories.OrderRepository
import com.wjoops.customer.domain.models.Basket
import com.wjoops.customer.domain.models.BasketItem
import com.wjoops.customer.domain.models.MenuItem
import com.wjoops.customer.domain.models.Order
import com.wjoops.customer.domain.models.OrderDraft
import com.wjoops.customer.domain.models.OrderStatus
import com.wjoops.customer.domain.models.basketTotals
import com.wjoops.customer.testutil.MainDispatcherRule
import com.wjoops.customer.ui.viewmodel.ConfirmOrderViewModel
import com.wjoops.customer.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ConfirmOrderViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun `placeOrder succeeds`() = runTest {
        val menuItem = MenuItem("m", "Item", "Desc", 100, true, true, "c")
        val basket = Basket(items = listOf(BasketItem(menuItem, 1)), totals = basketTotals(listOf(BasketItem(menuItem, 1))))
        val vm = ConfirmOrderViewModel(
            basketRepository = FakeBasketRepo(basket),
            orderRepository = FakeOrderRepo(),
        )

        var placed: Order? = null
        vm.placeOrder { placed = it }
        assertTrue(placed != null)
    }
}

private class FakeBasketRepo(basket: Basket) : BasketRepository {
    private val flow = MutableStateFlow(basket)
    override fun observeBasket() = flow.asStateFlow()
    override suspend fun addItem(item: MenuItem) {}
    override suspend fun removeItem(menuItemId: String) {}
    override suspend fun updateQty(menuItemId: String, quantity: Int) {}
    override suspend fun clear() {}
}

private class FakeOrderRepo : OrderRepository {
    override suspend fun placeOrder(draft: OrderDraft): ApiResult<Order> {
        val now = System.currentTimeMillis()
        return ApiResult.Success(
            Order(
                id = "o1",
                createdAt = now,
                status = OrderStatus.PENDING,
                items = draft.basket.items,
                totals = draft.basket.totals,
                estimatedReadyAt = now,
            ),
        )
    }

    override suspend fun getLatestOrder(): ApiResult<Order?> = ApiResult.Success(null)
    override suspend fun refreshOrderStatus(orderId: String): ApiResult<Order> = ApiResult.Error(com.wjoops.customer.util.ApiError.Unknown("stub"))
}
