package com.wjoops.customer.data.repositories

import com.wjoops.customer.domain.models.AuthState
import com.wjoops.customer.domain.models.Basket
import com.wjoops.customer.domain.models.MenuCategory
import com.wjoops.customer.domain.models.MenuItem
import com.wjoops.customer.domain.models.Order
import com.wjoops.customer.domain.models.OrderDraft
import com.wjoops.customer.domain.models.Restaurant
import com.wjoops.customer.domain.models.WaitTime
import com.wjoops.customer.util.ApiResult
import kotlinx.coroutines.flow.Flow

interface TokenProvider {
    suspend fun getAccessToken(): String?
}

interface AuthRepository {
    suspend fun requestOtp(phone: String): ApiResult<Unit>
    suspend fun verifyOtp(phone: String, otp: String): ApiResult<Unit>
    fun observeAuthState(): Flow<AuthState>
    suspend fun logout()
}

interface MenuRepository {
    suspend fun getRestaurant(): ApiResult<Restaurant>
    suspend fun getMenu(): ApiResult<Pair<List<MenuCategory>, List<MenuItem>>>
    suspend fun getWaitTime(): ApiResult<WaitTime>
}

interface BasketRepository {
    fun observeBasket(): Flow<Basket>
    suspend fun addItem(item: MenuItem)
    suspend fun removeItem(menuItemId: String)
    suspend fun updateQty(menuItemId: String, quantity: Int)
    suspend fun clear()
}

interface OrderRepository {
    suspend fun placeOrder(draft: OrderDraft): ApiResult<Order>
    suspend fun getLatestOrder(): ApiResult<Order?>
    suspend fun refreshOrderStatus(orderId: String): ApiResult<Order>
}
