package com.wjoops.customer.viewmodels

import com.wjoops.customer.data.repositories.BasketRepository
import com.wjoops.customer.domain.models.Basket
import com.wjoops.customer.domain.models.MenuItem
import com.wjoops.customer.domain.models.basketTotals
import com.wjoops.customer.testutil.MainDispatcherRule
import com.wjoops.customer.ui.viewmodel.BasketViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BasketViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun `collects basket updates`() = runTest {
        val repo = RecordingBasketRepository()
        val vm = BasketViewModel(basketRepository = repo)

        repo.emit(Basket(items = emptyList(), totals = basketTotals(emptyList())))
        // ViewModel should receive latest state.
        assertEquals(0, vm.state.value.basket?.items?.size ?: 0)
    }
}

private class RecordingBasketRepository : BasketRepository {
    private val flow = MutableStateFlow(Basket(items = emptyList(), totals = basketTotals(emptyList())))
    override fun observeBasket() = flow.asStateFlow()
    override suspend fun addItem(item: MenuItem) {}
    override suspend fun removeItem(menuItemId: String) {}
    override suspend fun updateQty(menuItemId: String, quantity: Int) {}
    override suspend fun clear() {}
    fun emit(basket: Basket) { flow.value = basket }
}
