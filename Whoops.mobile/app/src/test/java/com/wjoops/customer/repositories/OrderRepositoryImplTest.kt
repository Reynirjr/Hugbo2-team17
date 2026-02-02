package com.wjoops.customer.repositories

import com.wjoops.customer.data.repositories.OrderRepositoryImpl
import com.wjoops.customer.domain.models.OrderDraft
import com.wjoops.customer.domain.models.basketTotals
import com.wjoops.customer.domain.models.Basket
import com.wjoops.customer.testutil.MainDispatcherRule
import com.wjoops.customer.util.ApiResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class OrderRepositoryImplTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun `placeOrder returns an order`() = runTest {
        val repo = OrderRepositoryImpl()
        val basket = Basket(items = emptyList(), totals = basketTotals(emptyList()))
        val res = repo.placeOrder(OrderDraft(basket = basket))
        assertTrue(res is ApiResult.Success)
    }
}
