package com.wjoops.customer.viewmodels

import app.cash.turbine.test
import com.wjoops.customer.data.repositories.OrderRepository
import com.wjoops.customer.domain.models.Order
import com.wjoops.customer.domain.models.OrderDraft
import com.wjoops.customer.domain.models.OrderStatus
import com.wjoops.customer.domain.models.basketTotals
import com.wjoops.customer.domain.models.Basket
import com.wjoops.customer.testutil.MainDispatcherRule
import com.wjoops.customer.ui.viewmodel.OrderStatusViewModel
import com.wjoops.customer.util.ApiResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class OrderStatusViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun `load sets latest success`() = runTest {
        val vm = OrderStatusViewModel(orderRepository = FakeOrderRepositoryWithLatest())
        vm.state.test {
            val s1 = awaitItem()
            val s2 = awaitItem()
            assertTrue(s2.latest is com.wjoops.customer.util.UiState.Success)
            cancelAndConsumeRemainingEvents()
        }
    }
}

private class FakeOrderRepositoryWithLatest : OrderRepository {
    private val order = Order(
        id = "o1",
        createdAt = 0L,
        status = OrderStatus.PENDING,
        items = emptyList(),
        totals = basketTotals(emptyList()),
        estimatedReadyAt = 0L,
    )

    override suspend fun placeOrder(draft: OrderDraft): ApiResult<Order> = ApiResult.Success(order)
    override suspend fun getLatestOrder(): ApiResult<Order?> = ApiResult.Success(order)
    override suspend fun refreshOrderStatus(orderId: String): ApiResult<Order> = ApiResult.Success(order.copy(status = OrderStatus.READY))
}
